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
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import checkspec.api.Visibility;
import checkspec.report.ClassReport;
import checkspec.report.FieldReport;
import checkspec.report.MethodReport;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblem.Type;
import checkspec.spec.ClassSpec;
import checkspec.spec.FieldSpec;
import checkspec.spec.MethodSpec;
import checkspec.spec.ModifiersSpec;
import checkspec.spec.Spec;
import checkspec.spring.ResolvableType;
import checkspec.util.ClassUtils;

class StaticChecker {

	public static ClassReport checkImplements(Class<?> clazz, ClassSpec spec) {
		ClassReport report = new ClassReport(spec, clazz);
		
		report.addProblems(checkModifiers(clazz, spec));
		report.addSubReports(checkFields(clazz, spec));
		report.addSubReports(checkMethods(clazz, spec));

		return report;
	}

	public static List<MethodReport> checkMethods(Class<?> actual, ClassSpec spec) {
		// @formatter:off
		return Arrays.stream(spec.getDeclaredMethods()).parallel()
		             .sorted(Comparator.comparing(MethodSpec::getName))
		             .map(e -> checkMethod(actual, e))
		             .filter(e -> e != null)
		             .collect(Collectors.toList());
		// @formatter:on
	}

	private static MethodReport checkMethod(Class<?> actual, MethodSpec method) {
		String methodName = method.getName();

		Class<?>[] parameterTypes = Arrays.stream(method.getParameters()).parallel().map(Parameter::getType).toArray(Class[]::new);
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

			checkVisibility(actualMethod, method).ifPresent(report::addProblem);;
			report.addProblems(checkModifiers(actualMethod, method));

			return report;
		} catch (NoSuchMethodException | SecurityException ex) {
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

	private static List<ReportProblem> checkMethodParameters(Method actual, MethodSpec spec) {
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

	public static List<FieldReport> checkFields(Class<?> clazz, ClassSpec spec) {
		// @formatter:off
		return Arrays.stream(spec.getDeclaredFields())
		             .parallel()
		             .map(e -> checkField(clazz, e))
		             .collect(Collectors.toList());
		// @formatter:on
	}

	private static FieldReport checkField(Class<?> clazz, FieldSpec field) {
		String fieldName = field.getName();

		Class<?> fieldType = field.getType();
		String fieldTypeName = getName(fieldType);

		try {
			Field actualField = clazz.getDeclaredField(fieldName);
			Class<?> actualFieldType = actualField.getType();
			String actualFieldTypeName = getName(actualFieldType);

			FieldReport report = new FieldReport(field, actualField);
			
			if (actualFieldType != fieldType) {
				String format = "has type of \"%s\" rather than \"%s\"";
				String message = String.format(format, actualFieldTypeName, fieldTypeName);
				report.addProblem(new ReportProblem(1, message, Type.WARNING));
			}
			
			return report;
		} catch (NoSuchFieldException | SecurityException ex) {
			return new FieldReport(field);
		}
	}

	public static List<ReportProblem> checkModifiers(Class<?> actual, ClassSpec spec) {
		ModifiersSpec modifiersSpec = spec.getModifiers();
		return checkModifiers(actual.getModifiers(), modifiersSpec.getModifiers(), !modifiersSpec.isInterface() || actual.isInterface());
	}

	public static Optional<ReportProblem> checkVisibility(Class<?> actual, ClassSpec spec) {
		return checkVisibility(actual.getModifiers(), spec.getModifiers().getModifiers());
	}

	public static List<ReportProblem> checkModifiers(Member actual, Spec<? extends Member> spec) {
		boolean checkAbstract = !spec.getRawElement().getDeclaringClass().isInterface() || actual.getDeclaringClass().isInterface();
		return checkModifiers(actual.getModifiers(), spec.getModifiers().getModifiers(), checkAbstract);
	}

	public static Optional<ReportProblem> checkVisibility(Member actual, Spec<?> spec) {
		return checkVisibility(actual.getModifiers(), spec.getModifiers().getModifiers());
	}

	private static List<ReportProblem> checkModifiers(int actual, int spec, boolean checkAbstract) {
		List<Optional<ReportProblem>> problems = new ArrayList<>();

		if (checkAbstract) {
			problems.add(checkModifier(actual, spec, Modifier::isAbstract, ABSTRACT));
		}
		problems.add(checkModifier(actual, spec, Modifier::isFinal, FINAL));
		problems.add(checkModifier(actual, spec, Modifier::isNative, NATIVE));
		problems.add(checkModifier(actual, spec, Modifier::isStatic, STATIC));
		problems.add(checkModifier(actual, spec, Modifier::isStrict, STRICTFP));
		problems.add(checkModifier(actual, spec, Modifier::isSynchronized, SYNCHRONIZED));
		problems.add(checkModifier(actual, spec, Modifier::isTransient, TRANSIENT));
		problems.add(checkModifier(actual, spec, Modifier::isVolatile, VOLATILE));

		return problems.parallelStream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
	}

	public static Optional<ReportProblem> checkVisibility(int actual, int spec) {
		Visibility actualVisibility = getVisibility(actual);
		Visibility specVisibility = getVisibility(spec);

		if (actualVisibility != specVisibility) {
			ReportProblem problem;
			if (specVisibility == Visibility.PACKAGE) {
				problem = new ReportProblem(1, "should not have any visibility modifier", Type.ERROR);
			} else {
				problem = new ReportProblem(1, String.format("should have visibility \"%s\"", specVisibility), Type.ERROR);
			}
			return Optional.of(problem);
		}

		return Optional.empty();
	}

	private static Optional<ReportProblem> checkModifier(int actual, int spec, IntFunction<Boolean> function, javax.lang.model.element.Modifier modifier) {
		boolean actualRes = function.apply(actual);
		Boolean specRes = function.apply(spec);

		if (specRes && !actualRes) {
			String format = "should have modifier \"%s\"";
			ReportProblem problem = new ReportProblem(1, String.format(format, modifier), Type.WARNING);
			return Optional.of(problem);
		}

		if (!specRes && actualRes) {
			String format = "should not have modifier \"%s\"";
			ReportProblem problem = new ReportProblem(1, String.format(format, modifier), Type.WARNING);
			return Optional.of(problem);
		}

		return Optional.empty();
	}

	private static Comparator<Method> createMethodComparator(MethodSpec method) {
		ToIntFunction<Method> heuristic = actual -> compareMethods(actual, method);
		return Comparator.comparingInt(heuristic::applyAsInt);
	}

	private static int compareMethods(Method actual, MethodSpec spec) {
		if (actual.equals(spec.getRawElement())) {
			return 0;
		}

		int heuristic = 0;
		heuristic += checkModifiers(actual, spec).parallelStream().mapToLong(ReportProblem::getScore).sum();
		heuristic += checkMethodParameters(actual, spec).parallelStream().mapToLong(ReportProblem::getScore).sum();

		return heuristic;
	}
}
