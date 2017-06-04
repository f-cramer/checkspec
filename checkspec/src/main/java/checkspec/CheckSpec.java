package checkspec;

import static checkspec.StaticChecker.checkImplements;
import static checkspec.util.ClassUtils.getPackage;

import java.io.File;
import java.lang.annotation.Annotation;
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

public class CheckSpec {

	private static final String JAVA_CLASS_PATH = "java.class.path";
	private static final String PATH_SEPARATOR = "path.separator";

	private static volatile CheckSpec DEFAULT_INSTANCE;
	private static final Object DEFAULT_SYNC = new Object();

	private static volatile CheckSpec LIBRARY_LESS_INSTANCE;
	private static final Object LIBRARY_LESS_SYNC = new Object();

	public static CheckSpec getDefaultInstance() {
		if (DEFAULT_INSTANCE == null) {
			synchronized (DEFAULT_SYNC) {
				if (DEFAULT_INSTANCE == null) {
					//@formatter:off
					URL[] urls = Arrays.stream(System.getProperty(JAVA_CLASS_PATH)
					                                 .split(System.getProperty(PATH_SEPARATOR)))
                                       .flatMap(CheckSpec::getUrlAsStream)
                                       .toArray(URL[]::new);
					//@formatter:on
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
					//@formatter:off
					URL[] urls = Arrays.stream(System.getProperty(JAVA_CLASS_PATH)
					                                 .split(System.getProperty(PATH_SEPARATOR)))
					                   .filter(e -> !e.endsWith(".jar"))
                                       .flatMap(CheckSpec::getUrlAsStream)
                                       .toArray(URL[]::new);
					//@formatter:on
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
		Function<String, Stream<Class<?>>> classSupplier = ClassUtils.classStreamSupplier(new URLClassLoader(urls, ClassLoader.getSystemClassLoader()));
		
		return reflections.getAllTypes()
		                  .parallelStream()
		                  .flatMap(classSupplier)
//		                  .peek(System.out::println)
		                  .filter(CheckSpec::hasSpecAnnotation)
		                  .map(ClassSpecification::new)
		                  .toArray(ClassSpecification[]::new);
	}
	
	private static boolean hasSpecAnnotation(Class<?> clazz) {
		// @formatter:off
		return Arrays.stream(clazz.getAnnotations())
		             .parallel()
		             .map(Annotation::annotationType)
		             .map(Class::getName)
		             .anyMatch(StreamUtils.equalsPredicate(Spec.class.getName()));
		// @formatter:on
	}

	private static Reflections createReflections(URL[] urls) {
		// @formatter:off
		ConfigurationBuilder configuration = new ConfigurationBuilder()
				.forPackages("")
				.setUrls(urls)
				.setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner());
		// @formatter:on

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

	private final Reflections REFLECTIONS;

	private CheckSpec(URL[] urls) {
		REFLECTIONS = createReflections(urls);
	}

	public List<SpecReport> checkSpec() {
		// @formatter:off
		return REFLECTIONS.getTypesAnnotatedWith(Spec.class)
		                  .parallelStream()
		                  .map(ClassSpecification::new)
		                  .map(this::checkSpec)
		                  .collect(Collectors.toList());
		// @formatter:on
	}

	public SpecReport checkSpec(ClassSpecification spec) {
		return checkSpec(spec, "");
	}
	
	public SpecReport checkSpec(ClassSpecification spec, ClassLoader classLoader) {
		return checkSpec(spec, "", classLoader);
	}

	public SpecReport checkSpec(ClassSpecification spec, Class<?> basePackage) {
		return checkSpec(spec, getPackage(basePackage));
	}
	
	public SpecReport checkSpec(ClassSpecification spec, String basePackageName) {
		return checkSpec(spec, basePackageName, ClassLoader.getSystemClassLoader());
	}
	
	public SpecReport checkSpec(ClassSpecification spec, Class<?> basePackage, ClassLoader classLoader) {
		return checkSpec(spec, getPackage(basePackage), classLoader);
	}
	
	public SpecReport checkSpec(ClassSpecification spec, String basePackageName, ClassLoader classLoader) {
		Function<String, Stream<Class<?>>> loader = ClassUtils.classStreamSupplier(classLoader);
		String pkg = basePackageName.toLowerCase();
		
		// @formatter:off
		List<ClassReport> classReports = REFLECTIONS.getAllTypes()
		                                            .parallelStream()
		                                            .filter(e -> !e.equals(spec.getName()))
		                                            .filter(e -> getPackage(e).toLowerCase().startsWith(pkg))
		                                            .flatMap(loader)
		                                            .map(e -> checkImplements(e, spec))
		                                            .filter(ClassReport::hasAnyImplementation)
		                                            .sorted()
		                                            .collect(Collectors.toList());
		return new SpecReport(spec, classReports);
		// @formatter:on
	}
}
