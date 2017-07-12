package checkspec;

import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import com.google.common.base.Objects;

import checkspec.analysis.AnalysisForClass;
import checkspec.report.ClassReport;
import checkspec.report.SpecReport;
import checkspec.specification.ClassSpecification;
import checkspec.spring.ResolvableType;
import checkspec.util.ClassUtils;
import checkspec.util.ReflectionsUtils;
import checkspec.util.StreamUtils;
import checkspec.util.TypeDiscovery;
import lombok.NonNull;

@SuppressWarnings("unchecked")
public final class CheckSpec {

	private static final String JAR_SUFFIX = ".jar";

	private static volatile CheckSpec DEFAULT_INSTANCE;
	private static final Object DEFAULT_SYNC = new Object();

	private static volatile CheckSpec LIBRARY_LESS_INSTANCE;
	private static final Object LIBRARY_LESS_SYNC = new Object();

	private static final ClassLoader BOOT_CLASS_LOADER;

	static {
		ClassLoader loader = ClassUtils.getBaseClassLoader();
		while (loader.getParent() != null) {
			loader = loader.getParent();
		}

		BOOT_CLASS_LOADER = loader;
	}

	public static CheckSpec getDefaultInstance() {
		if (DEFAULT_INSTANCE == null) {
			synchronized (DEFAULT_SYNC) {
				if (DEFAULT_INSTANCE == null) {
					Reflections reflections = ReflectionsUtils.createDefaultReflections();
					ClassLoader classLoader = ClassUtils.getBaseClassLoader();
					DEFAULT_INSTANCE = new CheckSpec(reflections, classLoader);
				}
			}
		}
		return DEFAULT_INSTANCE;
	}

	public static CheckSpec getInstanceForClassPathWithoutJars() {
		if (LIBRARY_LESS_INSTANCE == null) {
			synchronized (LIBRARY_LESS_SYNC) {
				if (LIBRARY_LESS_INSTANCE == null) {
					URL[] urls = Arrays.stream(ReflectionsUtils.getUrlsFromClasspath()).parallel()
							.filter(url -> !url.getPath().endsWith(JAR_SUFFIX))
							.toArray(URL[]::new);

					Reflections reflections = ReflectionsUtils.createReflections(urls);
					ClassLoader classLoader = new URLClassLoader(urls, ClassUtils.getBaseClassLoader());
					LIBRARY_LESS_INSTANCE = new CheckSpec(reflections, classLoader);
				}
			}
		}
		return LIBRARY_LESS_INSTANCE;
	}

	public static CheckSpec getInstanceForClassPath(URL[] urls) {
		Reflections reflections = ReflectionsUtils.createReflections(urls);
		ClassLoader classLoader = new URLClassLoader(urls, ClassUtils.getBaseClassLoader());
		return new CheckSpec(reflections, classLoader);
	}

	public static <T> T createProxy(SpecReport report) {
		Class<?> clazz = report.getSpec().getRawElement().getRawClass();
		MethodInvocationHandler handler = StaticChecker.createInvocationHandler(clazz, report);
		return StaticChecker.createProxy(clazz, handler);
	}

	private static final String ERROR_FORMAT = "Analysis \"%s\" does not provide a default constructor and thus will not be used%n";
	private static final AnalysisForClass<?>[] ANALYSES;

	static {
		List<Class<?>> analyses = TypeDiscovery.getSubTypesOf(AnalysisForClass.class).stream()
				.filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
				.collect(Collectors.toList());

		List<Class<?>> mostConcreteAnalyses = new ArrayList<>();

		// filter for most concrete analyses only
		// any class that is subclassed is not used
		for (Class<?> analysis : analyses) {
			List<Class<?>> moreAbstractSuperClasses = mostConcreteAnalyses.parallelStream()
					.filter(potentialSuperClass -> ClassUtils.isSuperType(analysis, potentialSuperClass))
					.collect(Collectors.toList());

			boolean hasSubClassesAdded = mostConcreteAnalyses.parallelStream()
					.anyMatch(potentialSubClass -> ClassUtils.isSuperType(potentialSubClass, analysis));

			if (!hasSubClassesAdded) {
				mostConcreteAnalyses.add(analysis);
			}

			mostConcreteAnalyses.removeAll(moreAbstractSuperClasses);
		}

		ANALYSES = mostConcreteAnalyses.stream()
				.filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
				.map(clazz -> (Class<AnalysisForClass<?>>) clazz)
				.flatMap(ClassUtils.instantiate(ERROR_FORMAT))
				.peek(analysis -> System.out.println(analysis.getClass().getSimpleName()))
				.toArray(length -> new AnalysisForClass<?>[length]);
	}

	private final Reflections reflections;
	private final ClassLoader classLoader;

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
		URL specLocation = getLocation(spec.getRawElement().getRawClass());
		Collection<String> classNames = reflections.getStore().get(SubTypesScanner.class.getSimpleName()).values();
		List<Class<?>> possibleClasses = classNames.parallelStream()
				.filter(e -> ClassUtils.getPackage(e).toLowerCase().startsWith(basePackageName))
				.flatMap(ClassUtils.classStreamSupplier(classLoader))
				.filter(StreamUtils.equalsPredicate(specLocation, CheckSpec::getLocation).negate())
				.filter(this::loadedFromValidLocation)
				.collect(Collectors.toList());

		List<ClassReport> reports = Collections.emptyList();

		int maxIterations = 10;
		for (int iteration = 0; iteration <= maxIterations; iteration++) {
			List<ClassReport> oldReports = reports;
			reports = possibleClasses.parallelStream()
					.map(e -> checkImplements(e, spec, filterForBest(oldReports)))
					.collect(Collectors.toList());

			if (Objects.equal(oldReports, reports)) {
				break;
			}
		}

		List<ClassReport> classReports = reports.parallelStream()
				.filter(ClassReport::isAnyImplemenationMatching)
				.sorted()
				.collect(Collectors.toList());

		return new SpecReport(spec, classReports);
	}

	private static ClassReport checkImplements(Class<?> clazz, ClassSpecification spec, Map<ClassSpecification, ClassReport> reports) {
		ClassReport report = new ClassReport(spec, clazz);
		ResolvableType type = ResolvableType.forClass(clazz);

		for (final AnalysisForClass<?> analysis : ANALYSES) {
			performAnalysis(analysis, type, spec, reports, report);
		}

		return report;
	}

	private static <ReturnType> void performAnalysis(AnalysisForClass<ReturnType> analysis, ResolvableType clazz, ClassSpecification spec, Map<ClassSpecification, ClassReport> reports, ClassReport report) {
		ReturnType returnValue = analysis.analyze(clazz, spec, reports);
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

		return BOOT_CLASS_LOADER.getResource(canonicalName);
	}

	private static Map<ClassSpecification, ClassReport> filterForBest(List<ClassReport> reports) {
		return reports.parallelStream()
				.collect(Collectors.toMap(ClassReport::getSpec, Function.identity(), CheckSpec::merge));
	}

	private static ClassReport merge(ClassReport l1, ClassReport l2) {
		if (l1.getScore() <= l2.getScore()) {
			return l1;
		} else {
			return l2;
		}
	}
}
