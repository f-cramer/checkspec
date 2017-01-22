package checkspec;

import static checkspec.StaticChecker.checkFields;
import static checkspec.StaticChecker.checkMethods;
import static checkspec.StaticChecker.checkModifiers;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import checkspec.report.ErrorReport;

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

	public List<Pair<Class<?>, ErrorReport>> checkSpec(Class<?> interf) {
		if (!interf.isInterface()) {
			throw new IllegalArgumentException();
		}

		// @formatter:off
		Stream<Pair<Class<?>, ErrorReport>> mapped = REFLECTIONS.getAllTypes().parallelStream()
			.filter(e -> !e.equals(interf.getName()))
			.distinct()
			.filter(e -> !getPackage(e).startsWith(CHECK_SPEC_PACKAGE))
			.map(StaticChecker::getClass)
			.filter(e -> e != null)
			.map(e -> Pair.of(e, checkImplements(e, interf)));

		List<Pair<Class<?>, ErrorReport>> allPossibleClasses = mapped
			.sorted(Comparator.comparing(Pair::getRight))
//			.filter(e -> e.getRight().isSuccess())
			.collect(Collectors.toList());
		// @formatter:on

		// @formatter:off
		System.out.println(allPossibleClasses.parallelStream()
//			.map(e -> String.format("%s: %d%n%s", e.getLeft(), e.getRight().getErrorScore(), e.getRight()))
			.map(Pair::getRight)
			.map(ErrorReport::toString)
			.collect(Collectors.joining("\n")));
		// @formatter:on

		return allPossibleClasses;
	}

	private ErrorReport checkImplements(Class<?> clazz, Class<?> interf) {
		ErrorReport report = ErrorReport.success(clazz.toString());

		report.addEntries(checkModifiers(clazz, interf));
		report.addEntries(checkMethods(clazz, interf));
		report.addEntries(checkFields(clazz, interf));

		return report;
	}

	private static String getPackage(String className) {
		int lastIndexOfDot = className.lastIndexOf('.');
		return lastIndexOfDot >= 0 ? className.substring(0, lastIndexOfDot) : "";
	}
}
