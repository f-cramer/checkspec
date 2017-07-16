package checkspec;

import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import com.google.common.base.Objects;

import checkspec.analysis.ClassAnalysis;
import checkspec.report.ClassReport;
import checkspec.report.SpecReport;
import checkspec.specification.ClassSpecification;
import checkspec.type.ResolvableType;
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
	private static final ClassAnalysis<?>[] ANALYSES;

	static {
		List<Class<?>> analyses = TypeDiscovery.getSubTypesOf(ClassAnalysis.class).stream()
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
				.map(clazz -> (Class<ClassAnalysis<?>>) clazz)
				.flatMap(ClassUtils.instantiate(ERROR_FORMAT))
				.toArray(length -> new ClassAnalysis<?>[length]);
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
	 * @param specs
	 *            the non-null specification the return {@code SpecReport}
	 *            should be based on
	 * @return a {@code SpecReport} that is populated with a {@code ClassReport}
	 *         for any class that in any way matches {@code spec}
	 */
	public List<SpecReport> checkSpec(@NonNull Collection<ClassSpecification> specs) {
		return checkSpec(specs, "");
	}

	public List<SpecReport> checkSpec(@NonNull Collection<ClassSpecification> specs, @NonNull Class<?> basePackage) {
		return checkSpec(specs, ClassUtils.getPackage(basePackage));
	}

	public List<SpecReport> checkSpec(@NonNull Collection<ClassSpecification> specs, @NonNull String basePackageName) {
		List<Class<?>> possibleClasses = getPossibleClasses(specs, basePackageName);

		List<SpecReport> reports = Collections.emptyList();

		int maxIterations = 10;
		for (int iteration = 0; iteration <= maxIterations; iteration++) {
			List<SpecReport> oldReports = reports;
			MultiValuedMap<Class<?>, Class<?>> bestMatches = convert(oldReports);
			reports = performSpecs(specs, possibleClasses, bestMatches);

			if (Objects.equal(oldReports, reports)) {
				break;
			}
		}

		return reports.parallelStream()
				.map(CheckSpec::filterImproperClassReports)
				.collect(Collectors.toList());
	}

	private static List<SpecReport> performSpecs(Collection<ClassSpecification> specs, List<Class<?>> possibleClasses, MultiValuedMap<Class<?>, Class<?>> bestMatches) {
		return specs.parallelStream()
				.map(spec -> performSingleCheck(spec, possibleClasses, bestMatches))
				.collect(Collectors.toList());
	}

	private static SpecReport performSingleCheck(ClassSpecification spec, List<Class<?>> possibleClasses, MultiValuedMap<Class<?>, Class<?>> bestMatches) {
		List<ClassReport> reports = possibleClasses.parallelStream()
				.map(e -> checkImplements(e, spec, bestMatches))
				.collect(Collectors.toList());
		return new SpecReport(spec, reports);
	}

	private static ClassReport checkImplements(Class<?> clazz, ClassSpecification spec, MultiValuedMap<Class<?>, Class<?>> oldMappings) {
		ClassReport report = new ClassReport(spec, clazz);
		ResolvableType type = ResolvableType.forClass(clazz);

		for (final ClassAnalysis<?> analysis : ANALYSES) {
			performAnalysis(analysis, type, spec, oldMappings, report);
		}

		return report;
	}

	private static <ReturnType> void performAnalysis(ClassAnalysis<ReturnType> analysis, ResolvableType clazz, ClassSpecification spec, MultiValuedMap<Class<?>, Class<?>> reports, ClassReport report) {
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

	private static MultiValuedMap<Class<?>, Class<?>> convert(List<SpecReport> reports) {

		HashSetValuedHashMap<Class<?>, Class<?>> multimap = new HashSetValuedHashMap<>();
		reports.forEach(report -> multimap.putAll(report.getSpec().getRawElement().getRawClass(), getImplementationClasses(report)));
		return multimap;
	}

	private static List<Class<?>> getImplementationClasses(SpecReport report) {
		return report.getClassReports().parallelStream()
				.map(ClassReport::getImplementation)
				.map(ResolvableType::getRawClass)
				.collect(Collectors.toList());
	}

	private List<Class<?>> getPossibleClasses(Collection<ClassSpecification> specs, String basePackageName) {
		List<URL> specLocations = specs.parallelStream()
				.map(spec -> getLocation(spec.getRawElement().getRawClass()))
				.collect(Collectors.toList());
		Collection<String> classNames = reflections.getStore().get(SubTypesScanner.class.getSimpleName()).values();
		return classNames.parallelStream()
				.filter(e -> ClassUtils.getPackage(e).toLowerCase().startsWith(basePackageName))
				.flatMap(ClassUtils.classStreamSupplier(classLoader))
				.filter(StreamUtils.inPredicate(specLocations, CheckSpec::getLocation).negate())
				.filter(this::loadedFromValidLocation)
				.collect(Collectors.toList());
	}

	private static SpecReport filterImproperClassReports(SpecReport report) {
		List<ClassReport> classReports = report.getClassReports().parallelStream()
				.filter(ClassReport::isAnyImplemenationMatching)
				.sorted()
				.collect(Collectors.toList());
		return new SpecReport(report.getSpec(), classReports);
	}
}
