package checkspec;

import static checkspec.StaticChecker.checkMethods;
import static checkspec.StaticChecker.checkModifiers;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import test.Calc;

public class CheckSpec {

	private static final String CHECK_SPEC_PACKAGE = CheckSpec.class.getPackage().getName();
	private static final Reflections REFLECTIONS;

	static {
		// @formatter:off
		ConfigurationBuilder configuration = new ConfigurationBuilder()
				.forPackages("")
				.setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner());

		int availableProcessors = Runtime.getRuntime().availableProcessors();
		ExecutorService threadPool = Executors.newFixedThreadPool(availableProcessors, new DaemonThreadFactory());
		configuration.setExecutorService(threadPool);
		// @formatter:on
		REFLECTIONS = new Reflections(configuration);
	}

	public void checkSpec(Class<?> interf) {
		if (!interf.isInterface()) {
			throw new IllegalArgumentException();
		}

		// @formatter:off
		Set<?> allPossibleClasses = REFLECTIONS.getAllTypes().parallelStream()
			.filter(e -> !e.equals(interf.getName()))
			.filter(e -> !getPackage(e).startsWith(CHECK_SPEC_PACKAGE))
			.map(StaticChecker::getClass)
			.filter(e -> e != null)
			.filter(e -> checkImplements(e, interf))
			.collect(Collectors.toSet());
		// @formatter:on

		System.out.println(allPossibleClasses);
	}

	private boolean checkImplements(Class<?> clazz, Class<?> interf) {
		if (!checkModifiers(clazz, interf)) {
			return false;
		}

		if (!checkMethods()) {
			return false;
		}

		return true;
	}

	private static String getPackage(String className) {
		int lastIndexOfDot = className.lastIndexOf('.');
		return lastIndexOfDot >= 0 ? className.substring(0, lastIndexOfDot) : "";
	}

	public static void main(String[] args) {
		CheckSpec checkSpec = new CheckSpec();
		checkSpec.checkSpec(Calc.class);
	}
}
