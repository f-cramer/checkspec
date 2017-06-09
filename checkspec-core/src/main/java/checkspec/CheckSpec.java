package checkspec;

import static checkspec.StaticChecker.checkImplements;
import static checkspec.util.ClassUtils.getPackage;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import checkspec.api.Spec;
import checkspec.report.ClassReport;
import checkspec.report.SpecReport;
import checkspec.spec.ClassSpecification;
import checkspec.util.ClassUtils;
import checkspec.util.StreamUtils;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
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
					URL[] urls = Arrays.stream(System.getProperty(JAVA_CLASS_PATH).split(System.getProperty(PATH_SEPARATOR)))
							.flatMap(CheckSpec::getUrlAsStream)
							.toArray(URL[]::new);
					DEFAULT_INSTANCE = new CheckSpec(urls);
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
							.flatMap(CheckSpec::getUrlAsStream)
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
		Reflections reflections = createReflections(urls);
		Function<String, Stream<Class<?>>> classSupplier = ClassUtils.classStreamSupplier(new URLClassLoader(urls, ClassUtils.getSystemClassLoader()));

		return reflections.getAllTypes().parallelStream()
				.flatMap(classSupplier)
				.filter(CheckSpec::hasSpecAnnotation)
				.map(ClassSpecification::new)
				.toArray(ClassSpecification[]::new);
	}

	private static boolean hasSpecAnnotation(Class<?> clazz) {
		Function<Annotation, String> concat = StreamUtils.concat(Annotation::annotationType, Class::getName);

		return Arrays.stream(clazz.getAnnotations()).parallel()
				.filter(StreamUtils.equalsPredicate(Spec.class.getName(), concat))
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

	private static Reflections createReflections(URL[] urls) {
		ConfigurationBuilder configuration = new ConfigurationBuilder()
				.forPackages("")
				.setUrls(urls)
				.setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner());

		int availableProcessors = Runtime.getRuntime().availableProcessors();
		ExecutorService threadPool = Executors.newFixedThreadPool(availableProcessors, new DaemonThreadFactory());
		configuration.setExecutorService(threadPool);
		return new Reflections(configuration);
	}

	private static Stream<URL> getUrlAsStream(String path) {
		try {
			return Stream.of(new File(path).toURI().toURL());
		} catch (Exception e) {
			return Stream.empty();
		}
	}

	public static <T> T createProxy(SpecReport report) {
		Class<?> clazz = report.getSpec().getRawElement().getRawClass();
		MethodInvocationHandler handler = StaticChecker.createInvocationHandler(clazz, report);
		return StaticChecker.createProxy(clazz, handler);
	}

	private final Reflections reflections;
	private final ClassLoader classLoader;

	private CheckSpec(URL[] urls) {
		this(createReflections(urls), new URLClassLoader(urls, ClassUtils.getSystemClassLoader()));
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
		return checkSpec(spec, getPackage(basePackage));
	}

	public SpecReport checkSpec(@NonNull ClassSpecification spec, @NonNull String basePackageName) {
		List<ClassReport> classReports = reflections.getAllTypes().parallelStream()
//				.filter(StreamUtils.equalsPredicate(spec.getName()).negate())
				.filter(e -> getPackage(e).toLowerCase().startsWith(basePackageName))
				.flatMap(ClassUtils.classStreamSupplier(classLoader))
				.filter(StreamUtils.equalsPredicate(getLocation(spec.getRawElement().getRawClass()), CheckSpec::getLocation).negate())
				.map(e -> checkImplements(e, spec))
				.filter(ClassReport::hasAnyImplementation)
				.sorted()
				.collect(Collectors.toList());

		return new SpecReport(spec, classReports);
	}
	
	private static URL getLocation(@NonNull Class<?> clazz) {
		String canonicalName;
		Class<?> c1 = clazz;
		
		while (c1 != null && c1.getCanonicalName() == null) {
			c1 = c1.getEnclosingClass();
		}

		if (c1 == null) {
			return null;
		}
		
		canonicalName = c1.getCanonicalName().replace('.', '/') + ".class";

		ClassLoader loader = clazz.getClassLoader();
		if (loader != null) {
			return loader.getResource(canonicalName);
		}

		return BOOT_CLASS_LOADER.getResource(canonicalName);
	}
}
