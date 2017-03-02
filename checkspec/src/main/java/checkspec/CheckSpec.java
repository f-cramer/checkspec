package checkspec;

import static checkspec.StaticChecker.checkImplements;
import static checkspec.util.ClassUtils.getPackage;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import checkspec.annotation.Spec;
import checkspec.report.ClassReport;
import checkspec.report.SpecReport;
import checkspec.util.ClassUtils;

public class CheckSpec {

	private static final String CHECK_SPEC_PACKAGE = CheckSpec.class.getPackage().getName();
	private static final Reflections REFLECTIONS;

	static {
		Collection<URL> urls = Arrays.stream(System.getProperty("java.class.path").split(System.getProperty("path.separator")))
		                             .filter(e -> !e.endsWith(".jar"))
		                             .flatMap(CheckSpec::getUrlAsStream)
		                             .peek(System.out::println)
		                             .collect(Collectors.toSet());
		
		// @formatter:off
		ConfigurationBuilder configuration = new ConfigurationBuilder()
				.forPackages("")
				.addUrls(urls)
				.setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner());
		// @formatter:on
		


		int availableProcessors = Runtime.getRuntime().availableProcessors();
		ExecutorService threadPool = Executors.newFixedThreadPool(availableProcessors, new DaemonThreadFactory());
		configuration.setExecutorService(threadPool);
		REFLECTIONS = new Reflections(configuration);
	}

	public List<SpecReport> checkSpec() {
		// @formatter:off
		return REFLECTIONS.getTypesAnnotatedWith(Spec.class)
		                  .parallelStream()
		                  .map(this::checkSpec)
		                  .collect(Collectors.toList());
		// @formatter:on
	}

	public SpecReport checkSpec(Class<?> spec) {
		String specPackage = getPackage(spec);
		
		// @formatter:off
		List<ClassReport> classReports = REFLECTIONS.getAllTypes()
		                                            .parallelStream()
		                                            .distinct()
		                                            .peek(System.out::println)
		                                            .filter(e -> !e.equals(spec.getName()))
		                                            .filter(e -> !getPackage(e).startsWith(CHECK_SPEC_PACKAGE) || getPackage(e).startsWith(specPackage))
		                                            .flatMap(ClassUtils::getClassAsStream)
		                                            .map(e -> checkImplements(e, spec))
		                                            .filter(ClassReport::hasAnyImplementation)
		                                            .sorted()
		                                            .collect(Collectors.toList());
		return new SpecReport(spec, classReports);
		// @formatter:on
	}
	
	private static Stream<URL> getUrlAsStream(String path) {
		try {
			return Stream.of(new File(path).toURI().toURL());
		} catch (Exception e) {
			return Stream.empty();
		}
	}
}
