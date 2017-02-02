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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import checkspec.report.ClassReport;
import checkspec.report.MethodReport;
import checkspec.report.Report;
import checkspec.util.Visibility;

class StaticChecker {

	public static ClassReport checkImplements(Class<?> clazz, Class<?> spec) {
		ClassReport report = new ClassReport(spec, clazz);

		report.addEntries(checkModifiers(clazz, spec));
		report.addEntries(checkFields(clazz, spec));
		report.addEntries(checkMethods(clazz, spec));

		return report;
	}

	public static ClassReport checkMethods(Class<?> actual, Class<?> spec) {
		ClassReport report = new ClassReport(spec, actual);

		// @formatter:off
		Arrays.stream(spec.getDeclaredMethods()).parallel()
	                      .sorted(Comparator.comparing(Method::getName))
	                      .map(e -> checkMethod(actual, e))
	                      .forEachOrdered(report::add);
		// @formatter:on
		return report;
	}

	private static MethodReport checkMethod(Class<?> actual, Method method) {
		String methodName = method.getName();

		Class<?>[] parameterTypes = method.getParameterTypes();
		Class<?> methodReturnType = method.getReturnType();

		try {
			Method actualMethod = actual.getDeclaredMethod(methodName, parameterTypes);
			Class<?> actualMethodReturnType = actualMethod.getReturnType();
			String actualMethodReturnTypeName = getName(actualMethodReturnType);

			MethodReport report = new MethodReport(method, actualMethod);

			if (actualMethodReturnType != methodReturnType) {
				boolean compatible = org.apache.commons.lang3.ClassUtils.isAssignable(actualMethodReturnType, methodReturnType);
				String format = "returns " + (compatible ? "compatible" : "incompatible") + " type \"%s\"";
				report.addError(String.format(format, actualMethodReturnTypeName));
			}

			report.addEntries(checkVisibility(actualMethod, method));
			report.addEntries(checkModifiers(actualMethod, method));

			return report;
		} catch (NoSuchMethodException | SecurityException ex) {
			List<Method> methodsWithEqualName = Arrays.stream(actual.getDeclaredMethods()).parallel().filter(e -> e.getName().equals(methodName)).collect(Collectors.toList());

			if (methodsWithEqualName.isEmpty()) {
				return new MethodReport(method);
			} else {
				Method actualMethod = methodsWithEqualName.parallelStream().min(createMethodComparator(method)).get();

				MethodReport report = new MethodReport(method, actualMethod);
				report.addEntries(checkMethodParameters(actualMethod.getParameters(), method.getParameters()));

				return report;
			}
		}
	}

	private static Report<?> checkMethodParameters(Parameter[] actual, Parameter[] spec) {
		Report<?> report = Report.success();

		int actualLength = actual.length;
		int specLength = spec.length;

		if (actualLength == specLength) {
			for (int i = 0; i < actualLength; i++) {
				Class<?> specType = spec[i].getType();
				Class<?> actualType = actual[i].getType();

				if (actualType != specType) {
					boolean compatible = org.apache.commons.lang3.ClassUtils.isAssignable(specType, actualType);
					String format = "parameter %d has " + (compatible ? "compatible" : "incompatible") + " type \"%s\"";
					report.addError(String.format(format, i + 1, getName(actualType)));
				}
			}
		} else {
			report.addError(Math.abs(actualLength - specLength), String.format("parameter count should be %s but is %s", specLength, actualLength));
		}

		return report;
	}

	public static Report<?> checkFields(Class<?> clazz, Class<?> interf) {
		Report<?> report = Report.success();
		Arrays.stream(interf.getDeclaredFields()).parallel().map(e -> checkField(clazz, e)).forEachOrdered(report::addEntries);
		return report;
	}

	private static Report<?> checkField(Class<?> clazz, Field field) {
		String fieldName = field.getName();

		Class<?> fieldType = field.getType();
		String fieldTypeName = getName(fieldType);
		Visibility fieldVisibility = getVisibility(field.getModifiers());

		Report<?> report = Report.success(String.format("%s %s %s", fieldVisibility, fieldTypeName, fieldName));

		try {
			Field actualField = clazz.getDeclaredField(fieldName);
			Class<?> actualFieldType = actualField.getType();
			String actualFieldTypeName = getName(actualFieldType);

			if (actualFieldType != fieldType) {
				String format = "has type of \"%s\" rather than \"%s\"";
				String errorMessage = String.format(format, actualFieldTypeName, fieldTypeName);
				report.addError(errorMessage);
			}
		} catch (NoSuchFieldException | SecurityException ex) {
			report.addError("was not found");
		}

		return report;
	}

	public static Report<?> checkModifiers(Class<?> actual, Class<?> spec) {
		return checkModifiers(actual.getModifiers(), spec.getModifiers(), !spec.isInterface() || actual.isInterface());
	}

	public static Report<?> checkVisibility(Class<?> actual, Class<?> spec) {
		return checkVisibility(actual.getModifiers(), spec.getModifiers());
	}

	public static Report<?> checkModifiers(Member actual, Member spec) {
		boolean checkAbstract = !spec.getDeclaringClass().isInterface() || actual.getDeclaringClass().isInterface();
		return checkModifiers(actual.getModifiers(), spec.getModifiers(), checkAbstract);
	}

	public static Report<?> checkVisibility(Member actual, Member spec) {
		return checkVisibility(actual.getModifiers(), spec.getModifiers());
	}

	private static Report<?> checkModifiers(int actual, int spec, boolean checkAbstract) {
		Report<?> report = Report.success();

		if (checkAbstract) {
			report.addEntries(checkModifier(actual, spec, Modifier::isAbstract, ABSTRACT));
		}
		report.addEntries(checkModifier(actual, spec, Modifier::isFinal, FINAL));
		report.addEntries(checkModifier(actual, spec, Modifier::isNative, NATIVE));
		report.addEntries(checkModifier(actual, spec, Modifier::isStatic, STATIC));
		report.addEntries(checkModifier(actual, spec, Modifier::isStrict, STRICTFP));
		report.addEntries(checkModifier(actual, spec, Modifier::isSynchronized, SYNCHRONIZED));
		report.addEntries(checkModifier(actual, spec, Modifier::isTransient, TRANSIENT));
		report.addEntries(checkModifier(actual, spec, Modifier::isVolatile, VOLATILE));

		return report;
	}

	public static Report<?> checkVisibility(int actual, int spec) {
		Visibility actualVisibility = getVisibility(actual);
		Visibility specVisibility = getVisibility(spec);

		if (actualVisibility != specVisibility) {
			if (specVisibility == Visibility.DEFAULT) {
				return Report.error("should not have any visibilty modifier");
			} else {
				return Report.error(String.format("should have visibility \"%s\"", specVisibility));
			}
		}

		return Report.success();
	}

	private static Report<?> checkModifier(int actual, int spec, IntFunction<Boolean> function, javax.lang.model.element.Modifier modifier) {
		boolean actualRes = function.apply(actual);
		Boolean specRes = function.apply(spec);

		if (specRes && !actualRes) {
			String format = "should have modifier \"%s\"";
			return Report.error("", String.format(format, modifier));
		}

		if (!specRes && actualRes) {
			String format = "should not have modifier \"%s\"";
			return Report.error("", String.format(format, modifier));
		}

		return Report.success();
	}

	private static Comparator<Method> createMethodComparator(Method method) {
		ToIntFunction<Method> heuristic = actual -> compareMethods(actual, method);
		return Comparator.comparingInt(heuristic::applyAsInt);
	}

	private static int compareMethods(Method actual, Method spec) {
		if (actual.equals(spec)) {
			return 0;
		}

		int heuristic = 0;
		heuristic += checkModifiers(actual, spec).getScore();
		heuristic += checkMethodParameters(actual.getParameters(), spec.getParameters()).getScore();

		return heuristic;
	}
}
