package checkspec;

import static checkspec.util.ClassUtils.getName;
import static checkspec.util.MemberUtils.getVisibility;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.NATIVE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.element.Modifier.STRICTFP;
import static javax.lang.model.element.Modifier.SYNCHRONIZED;
import static javax.lang.model.element.Modifier.TRANSIENT;
import static javax.lang.model.element.Modifier.VOLATILE;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import checkspec.api.Visibility;
import checkspec.report.ClassReport;
import checkspec.report.FieldReport;
import checkspec.report.MethodReport;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblem.Type;
import checkspec.spec.ClassSpecification;
import checkspec.spec.FieldSpecification;
import checkspec.spec.MethodParameterSpecification;
import checkspec.spec.MethodSpecification;
import checkspec.spec.ModifiersSpecification;
import checkspec.spec.ModifiersSpecification.State;
import checkspec.spec.Specification;
import checkspec.spec.VisibilitySpecification;
import checkspec.spring.ResolvableType;
import checkspec.util.ClassUtils;
import checkspec.util.FieldUtils;

class StaticChecker {

	public static ClassReport checkImplements(Class<?> clazz, ClassSpecification spec) {
		ClassReport report = new ClassReport(spec, clazz);

		report.addProblems(checkModifiers(clazz, spec));
		report.addSubReports(checkFields(clazz, spec));
		report.addSubReports(checkMethods(clazz, spec));

		return report;
	}

	public static List<MethodReport> checkMethods(Class<?> actual, ClassSpecification spec) {
		// @formatter:off
		return Arrays.stream(spec.getDeclaredMethods()).parallel()
		             .sorted(Comparator.comparing(MethodSpecification::getName))
		             .map(e -> checkMethod(actual, e))
		             .filter(e -> e != null)
		             .collect(Collectors.toList());
		// @formatter:on
	}

	private static MethodReport checkMethod(Class<?> actual, MethodSpecification method) {
		String methodName = method.getName();

		Class<?>[] parameterTypes = Arrays.stream(method.getParameters()).parallel().map(MethodParameterSpecification::getType).map(ResolvableType::getRawClass).toArray(Class[]::new);
		ResolvableType methodReturnType = ResolvableType.forMethodReturnType(method.getRawElement());

		try {
			Method actualMethod = actual.getDeclaredMethod(methodName, parameterTypes);
			ResolvableType actualMethodReturnType = ResolvableType.forMethodReturnType(actualMethod);
			String actualMethodReturnTypeName = getName(actualMethodReturnType);

			MethodReport report = new MethodReport(method, actualMethod);

			if (actualMethodReturnType.getRawClass() != methodReturnType.getRawClass()) {
				boolean compatible = ClassUtils.isAssignable(actualMethodReturnType, methodReturnType);
				String format = "returns " + (compatible ? "" : "in") + "compatible type \"%s\"";
				Type type = compatible ? Type.WARNING : Type.ERROR;
				report.addProblem(new ReportProblem(1, String.format(format, actualMethodReturnTypeName), type));
			}

			checkVisibility(actualMethod, method).ifPresent(report::addProblem);
			;
			report.addProblems(checkModifiers(actualMethod, method));

			return report;
		} catch (NoSuchMethodException ex) {
			List<Method> methodsWithEqualName = Arrays.stream(actual.getDeclaredMethods()).parallel().filter(e -> e.getName().equals(methodName)).collect(Collectors.toList());

			if (methodsWithEqualName.isEmpty()) {
				return new MethodReport(method);
			} else {
				Method actualMethod = methodsWithEqualName.parallelStream().min(createMethodComparator(method)).get();

				MethodReport report = new MethodReport(method, actualMethod);
				report.addProblems(checkMethodParameters(actualMethod, method));

				return report;
			}
		} catch (NoClassDefFoundError e) {
			return null;
		}
	}

	private static List<ReportProblem> checkMethodParameters(Method actual, MethodSpecification spec) {
		List<ReportProblem> problems = new ArrayList<>();

		int actualLength = actual.getParameterCount();
		int specLength = spec.getParameters().length;

		if (actualLength == specLength) {
			for (int i = 0; i < actualLength; i++) {
				ResolvableType specType = ResolvableType.forMethodParameter(spec.getRawElement(), i);
				ResolvableType actualType = ResolvableType.forMethodParameter(actual, i);

				if (actualType.getRawClass() != specType.getRawClass()) {
					boolean compatible = ClassUtils.isAssignable(specType, actualType);
					String format = "parameter %d has " + (compatible ? "" : "in") + "compatible type \"%s\"";
					Type type = compatible ? Type.WARNING : Type.ERROR;
					problems.add(new ReportProblem(1, String.format(format, i + 1, getName(actualType)), type));
				}
			}
		} else {
			int score = Math.abs(actualLength - specLength);
			String message = String.format("parameter count should be %s but is %s", specLength, actualLength);
			problems.add(new ReportProblem(score, message, Type.WARNING));
		}

		return problems;
	}

	public static List<FieldReport> checkFields(Class<?> clazz, ClassSpecification spec) {
		// @formatter:off
		return Arrays.stream(spec.getDeclaredFields())
		             .parallel()
		             .map(e -> checkField(clazz, e))
		             .collect(Collectors.toList());
		// @formatter:on
	}

	private static FieldReport checkField(Class<?> clazz, FieldSpecification field) {
		String fieldName = field.getName();

		ResolvableType fieldType = field.getType();
		String fieldTypeName = getName(fieldType);

		try {
			Field actualField = clazz.getDeclaredField(fieldName);
			ResolvableType actualFieldType = FieldUtils.getType(actualField);
			String actualFieldTypeName = getName(actualFieldType);

			FieldReport report = new FieldReport(field, actualField);

			if (!actualFieldType.equals(fieldType)) {
				String format = "has type of \"%s\" rather than \"%s\"";
				String message = String.format(format, actualFieldTypeName, fieldTypeName);
				report.addProblem(new ReportProblem(1, message, Type.WARNING));
			}

			return report;
		} catch (NoSuchFieldException e) {
			return new FieldReport(field);
		}
	}

	public static List<ReportProblem> checkModifiers(Class<?> actual, ClassSpecification spec) {
		ModifiersSpecification modifiersSpec = spec.getModifiers();
		return checkModifiers(actual.getModifiers(), modifiersSpec, !modifiersSpec.isInterface() || actual.isInterface());
	}

	public static Optional<ReportProblem> checkVisibility(Class<?> actual, ClassSpecification spec) {
		return checkVisibility(actual.getModifiers(), spec.getVisibility());
	}

	public static List<ReportProblem> checkModifiers(Member actual, Specification<? extends Member> spec) {
		boolean checkAbstract = !spec.getRawElement().getDeclaringClass().isInterface() || actual.getDeclaringClass().isInterface();
		return checkModifiers(actual.getModifiers(), spec.getModifiers(), checkAbstract);
	}

	public static Optional<ReportProblem> checkVisibility(Member actual, Specification<?> spec) {
		return checkVisibility(actual.getModifiers(), spec.getVisibility());
	}

	private static List<ReportProblem> checkModifiers(int actual, ModifiersSpecification spec, boolean checkAbstract) {
		List<Optional<ReportProblem>> problems = new ArrayList<>();

		if (checkAbstract) {
			problems.add(checkModifier(Modifier.isAbstract(actual), spec.isAbstract(), ABSTRACT));
		}
		problems.add(checkModifier(Modifier.isFinal(actual), spec.isFinal(), FINAL));
		problems.add(checkModifier(Modifier.isNative(actual), spec.isNative(), NATIVE));
		problems.add(checkModifier(Modifier.isStatic(actual), spec.isStatic(), STATIC));
		problems.add(checkModifier(Modifier.isStrict(actual), spec.isStrict(), STRICTFP));
		problems.add(checkModifier(Modifier.isSynchronized(actual), spec.isSynchronized(), SYNCHRONIZED));
		problems.add(checkModifier(Modifier.isTransient(actual), spec.isTransient(), TRANSIENT));
		problems.add(checkModifier(Modifier.isVolatile(actual), spec.isVolatile(), VOLATILE));

		return problems.parallelStream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
	}

	public static Optional<ReportProblem> checkVisibility(int actualModifiers, VisibilitySpecification spec) {
		Visibility actualVisibility = getVisibility(actualModifiers);

		if (!spec.matches(actualVisibility)) {
			ReportProblem problem;
			Visibility[] visibilities = spec.getVisibilities();
			if (visibilities.length == 1 && visibilities[0] == Visibility.PACKAGE) {
				problem = new ReportProblem(1, "should not have any visibility modifier", Type.ERROR);
			} else if (visibilities.length == 1) {
				problem = new ReportProblem(1, String.format("should have visibility \"%s\"", visibilities[0]), Type.ERROR);
			} else {
				String visibilityString = Arrays.stream(visibilities).map(Visibility::toString).collect(Collectors.joining(", "));
				problem = new ReportProblem(1, String.format("should have any of the following visibilities: \"\"", visibilityString), Type.ERROR);
			}
			return Optional.of(problem);
		}

		return Optional.empty();
	}

	private static Optional<ReportProblem> checkModifier(boolean actual, State spec, javax.lang.model.element.Modifier modifier) {
		if (spec == State.TRUE && !spec.matches(actual)) {
			String format = "should have modifier \"%s\"";
			ReportProblem problem = new ReportProblem(1, String.format(format, modifier), Type.WARNING);
			return Optional.of(problem);
		}

		if (spec == State.FALSE && !spec.matches(actual)) {
			String format = "should not have modifier \"%s\"";
			ReportProblem problem = new ReportProblem(1, String.format(format, modifier), Type.WARNING);
			return Optional.of(problem);
		}

		return Optional.empty();
	}

	private static Comparator<Method> createMethodComparator(MethodSpecification method) {
		ToIntFunction<Method> heuristic = actual -> compareMethods(actual, method);
		return Comparator.comparingInt(heuristic::applyAsInt);
	}

	private static int compareMethods(Method actual, MethodSpecification spec) {
		if (actual.equals(spec.getRawElement())) {
			return 0;
		}

		int heuristic = 0;
		heuristic += checkModifiers(actual, spec).parallelStream().mapToLong(ReportProblem::getScore).sum();
		heuristic += checkMethodParameters(actual, spec).parallelStream().mapToLong(ReportProblem::getScore).sum();

		return heuristic;
	}
}
