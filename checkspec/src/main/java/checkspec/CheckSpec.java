package checkspec;

import static checkspec.StaticChecker.checkImplements;
import static checkspec.util.ClassUtils.getPackage;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
		// @formatter:off
		ConfigurationBuilder configuration = new ConfigurationBuilder()
				.forPackages("")
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
		// @formatter:off
		List<ClassReport> classReports = REFLECTIONS.getAllTypes()
		                  .parallelStream()
		                  .distinct()
		                  .filter(e -> !e.equals(spec.getName()) && !getPackage(e).startsWith(CHECK_SPEC_PACKAGE))
		                  .flatMap(ClassUtils::getClassAsStream)
		                  .map(e -> checkImplements(e, spec))
		                  .sorted()
		                  .collect(Collectors.toList());
		return new SpecReport(spec, classReports);
		// @formatter:on
	}
}
