package checkspec;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import checkspec.analysis.AnalysisForClass;
import checkspec.api.Spec;
import checkspec.report.ClassReport;
import checkspec.report.SpecReport;
import checkspec.spec.ClassSpecification;
import checkspec.spring.ResolvableType;
import checkspec.util.ClassUtils;
import checkspec.util.ReflectionsUtils;
import checkspec.util.StreamUtils;
import checkspec.util.TypeDiscovery;
import lombok.NonNull;

@SuppressWarnings("unchecked")
public final class CheckSpec {

	private static final String JAVA_CLASS_PATH = "java.class.path";
	private static final String PATH_SEPARATOR = "path.separator";

	private static volatile CheckSpec DEFAULT_INSTANCE;
	private static final Object DEFAULT_SYNC = new Object();

	private static volatile CheckSpec LIBRARY_LESS_INSTANCE;
	private static final Object LIBRARY_LESS_SYNC = new Object();

	private static final ClassLoader BOOT_CLASS_LOADER;

	static {
		ClassLoader loader = ClassUtils.getSystemClassLoader();
		while (loader.getParent() != null) {
			loader = loader.getParent();
		}

		BOOT_CLASS_LOADER = loader;
	}

	public static CheckSpec getDefaultInstance() {
		if (DEFAULT_INSTANCE == null) {
			synchronized (DEFAULT_SYNC) {
				if (DEFAULT_INSTANCE == null) {
					DEFAULT_INSTANCE = new CheckSpec();
				}
			}
		}
		return DEFAULT_INSTANCE;
	}

	public static CheckSpec getInstanceForClassPathWithoutJars() {
		if (LIBRARY_LESS_INSTANCE == null) {
			synchronized (LIBRARY_LESS_SYNC) {
				if (LIBRARY_LESS_INSTANCE == null) {
					URL[] urls = Arrays.stream(System.getProperty(JAVA_CLASS_PATH).split(System.getProperty(PATH_SEPARATOR)))
							.filter(e -> !e.endsWith(".jar"))
							.flatMap(ReflectionsUtils::getUrlAsStream)
							.toArray(URL[]::new);
					LIBRARY_LESS_INSTANCE = new CheckSpec(urls);
				}
			}
		}
		return LIBRARY_LESS_INSTANCE;
	}

	public static CheckSpec getInstanceForClassPath(URL[] classPathEntries) {
		return new CheckSpec(classPathEntries);
	}

	public static ClassSpecification[] findSpecifications(URL[] urls) {
		Reflections reflections = ReflectionsUtils.createReflections(urls);
		Function<String, Stream<Class<?>>> classSupplier = ClassUtils.systemClassStreamSupplier();

		return reflections.getAllTypes().parallelStream()
				.flatMap(classSupplier)
				.filter(CheckSpec::hasSpecAnnotation)
				.map(ClassSpecification::new)
				.toArray(ClassSpecification[]::new);
	}

	private static boolean hasSpecAnnotation(Class<?> clazz) {
		Function<Annotation, String> annotationName = ((Function<Annotation, Class<?>>) Annotation::annotationType).andThen(Class::getName);

		return Arrays.stream(clazz.getAnnotations()).parallel()
				.filter(StreamUtils.equalsPredicate(Spec.class.getName(), annotationName))
				.anyMatch(CheckSpec::hasValueSetToTrue);
	}

	private static boolean hasValueSetToTrue(Annotation annotation) {
		Method valueMethod;
		try {
			valueMethod = annotation.annotationType().getMethod("value");
		} catch (NoSuchMethodException | SecurityException e) {
			return false;
		}

		if (valueMethod != null && valueMethod.getReturnType() == boolean.class) {
			boolean accessible = valueMethod.isAccessible();
			valueMethod.setAccessible(true);
			try {
				return (Boolean) valueMethod.invoke(annotation);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException expected) {
			}
			valueMethod.setAccessible(accessible);
		}

		return false;
	}

	public static <T> T createProxy(SpecReport report) {
		Class<?> clazz = report.getSpec().getRawElement().getRawClass();
		MethodInvocationHandler handler = StaticChecker.createInvocationHandler(clazz, report);
		return StaticChecker.createProxy(clazz, handler);
	}

	private static final String ERROR_FORMAT = "Analysis \"%s\" does not provide a default constructor and thus will not be used%n";
	private static final AnalysisForClass<?>[] ANALYSES;

	static {
		ANALYSES = TypeDiscovery.getSubTypesOf(AnalysisForClass.class).stream()
				.filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
				.map(clazz -> (Class<AnalysisForClass<?>>) clazz)
				.flatMap(ClassUtils.instantiate(ERROR_FORMAT))
				.toArray(length -> new AnalysisForClass<?>[length]);
	}

	private final Reflections reflections;
	private final ClassLoader classLoader;

	private CheckSpec() {
		this(ReflectionsUtils.createDefaultReflections(), ClassUtils.getSystemClassLoader());
	}

	private CheckSpec(URL[] urls) {
		this(ReflectionsUtils.createReflections(urls), new URLClassLoader(urls, ClassUtils.getSystemClassLoader()));
	}

	private CheckSpec(Reflections reflections, ClassLoader classLoader) {
		this.reflections = reflections;
		this.classLoader = classLoader;

		this.reflections.expandSuperTypes();
	}

	/**
	 * Creates a {@link SpecReport} for the given specification {@code spec}
	 * that is populated with a {@link ClassReport} for any class that in any
	 * way matches {@code spec}.
	 * <p>
	 * Behaves the same as a call to {@code checkSpec(spec, "")}.
	 *
	 * @param spec
	 *            the non-null specification the return {@code SpecReport}
	 *            should be based on
	 * @return a {@code SpecReport} that is populated with a {@code ClassReport}
	 *         for any class that in any way matches {@code spec}
	 */
	public SpecReport checkSpec(@NonNull ClassSpecification spec) {
		return checkSpec(spec, "");
	}

	public SpecReport checkSpec(@NonNull ClassSpecification spec, @NonNull Class<?> basePackage) {
		return checkSpec(spec, ClassUtils.getPackage(basePackage));
	}

	public SpecReport checkSpec(@NonNull ClassSpecification spec, @NonNull String basePackageName) {
		List<ClassReport> classReports = reflections.getStore().get(SubTypesScanner.class.getSimpleName()).values().parallelStream()
				.filter(e -> ClassUtils.getPackage(e).toLowerCase().startsWith(basePackageName))
				.flatMap(ClassUtils.classStreamSupplier(classLoader))
				.filter(StreamUtils.equalsPredicate(getLocation(spec.getRawElement().getRawClass()), CheckSpec::getLocation).negate())
				.filter(this::loadedFromValidLocation)
				.map(e -> checkImplements(e, spec))
				.filter(ClassReport::hasAnyImplementation)
				.sorted()
				.collect(Collectors.toList());

		return new SpecReport(spec, classReports);
	}

	private static ClassReport checkImplements(Class<?> clazz, ClassSpecification spec) {
		ClassReport report = new ClassReport(spec, clazz);
		ResolvableType type = ResolvableType.forClass(clazz);

		for (final AnalysisForClass<?> analysis : ANALYSES) {
			performAnalysis(analysis, type, spec, report);
		}

		return report;
	}

	private static <ReturnType> void performAnalysis(AnalysisForClass<ReturnType> analysis, ResolvableType clazz, ClassSpecification spec, ClassReport report) {
		ReturnType returnValue = analysis.analyze(clazz, spec);
		analysis.add(report, returnValue);
	}

	private boolean loadedFromValidLocation(@NonNull Class<?> clazz) {
		Set<URL> urls = reflections.getConfiguration().getUrls();
		URL location = getLocation(clazz);

		return urls.parallelStream().anyMatch(url -> isParent(location, url));
	}

	private static boolean isParent(@NonNull URL child, @NonNull URL parent) {
		return child.getPath().startsWith(parent.getPath());
	}

	private static URL getLocation(@NonNull Class<?> clazz) {
		String canonicalName;
		Class<?> c1 = clazz;

		while (c1 != null && c1.getEnclosingClass() != null && c1.getCanonicalName() == null) {
			c1 = c1.getEnclosingClass();
		}

		if (c1 == null) {
			return null;
		}

		canonicalName = c1.getName().replace('.', '/') + ".class";

		ClassLoader loader = clazz.getClassLoader();
		if (loader == null) {
			loader = c1.getClassLoader();
		}
			
		if (loader != null) {
			URL resource = loader.getResource(canonicalName);
			if (resource != null) {
				return resource;
			}
		}

		URL resource = BOOT_CLASS_LOADER.getResource(canonicalName);
		if (resource == null) {
			System.out.println(canonicalName);
		}
		return resource;
	}
}
