package checkspec;

import static checkspec.ClassUtils.getName;
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

import checkspec.report.ErrorReport;

public class StaticChecker {

	public static Class<?> getClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static ErrorReport checkMethods(Class<?> actual, Class<?> spec) {
		ErrorReport report = ErrorReport.success();
		Arrays.stream(spec.getDeclaredMethods()).parallel().sorted(Comparator.comparing(Method::getName)).map(e -> checkMethod(actual, e)).forEachOrdered(report::add);
		return report;
	}

	private static ErrorReport checkMethod(Class<?> actual, Method method) {
		String methodName = method.getName();

		Class<?>[] parameterTypes = method.getParameterTypes();
		String parameterList = toParameterList(parameterTypes);

		ErrorReport report = ErrorReport.success(String.format("method %s(%s)", methodName, parameterList));

		Class<?> methodReturnType = method.getReturnType();
		String methodReturnTypeName = getName(methodReturnType);

		try {
			Method actualMethod = actual.getDeclaredMethod(methodName, parameterTypes);
			Class<?> actualMethodReturnType = actualMethod.getReturnType();
			String actualMethodReturnTypeName = getName(actualMethodReturnType);

			if (actualMethodReturnType != methodReturnType) {
				String format = "should have return type \"%s\" instead of \"%s\"";
				String errorMessage = String.format(format, methodReturnTypeName, actualMethodReturnTypeName);
				report.addError(errorMessage);
			}

			report.addEntries(checkVisibility(actualMethod, method));
			report.addEntries(checkModifiers(actualMethod, method));
		} catch (NoSuchMethodException | SecurityException ex) {
			List<Method> methodsWithEqualName = Arrays.stream(actual.getDeclaredMethods()).parallel().filter(e -> e.getName().equals(methodName)).collect(Collectors.toList());

			if (methodsWithEqualName.isEmpty()) {
				String format = "was not found";
				String errorMessage = String.format(format, methodName, parameterList);
				report.addError(errorMessage);
			} else {
				String format = "but method with equal name but different parameter list: \"%s(%s)\"";

				Method actualMethod = methodsWithEqualName.parallelStream().min(createMethodComparator(method)).get();
				String errorMessage = String.format(format, actualMethod.getName(), toParameterList(actualMethod.getParameterTypes()));
				report.addError(errorMessage);
			}
		}

		return report;
	}

	private static ErrorReport checkMethodParameters(Parameter[] actual, Parameter[] spec) {
		ErrorReport report = ErrorReport.success();

		int actualLength = actual.length;
		int specLength = spec.length;

		if (actualLength == specLength) {
			for (int i = 0; i < actualLength; i++) {
				Class<?> specType = spec[i].getType();
				Class<?> actualType = actual[i].getType();

				if (actualType != specType) {
					report.addError(String.format("parameter %d of type \"%s\" does not match the required type \"%s\"", i + 1, getName(actualType), getName(specType)));
				}
			}
		} else {
			report.addError(Math.abs(actualLength - specLength), String.format("parameter could should be %s but is %s", specLength, actualLength));
		}

		return report;
	}

	public static ErrorReport checkFields(Class<?> clazz, Class<?> interf) {
		ErrorReport report = ErrorReport.success();
		Arrays.stream(interf.getDeclaredFields()).parallel().map(e -> checkField(clazz, e)).forEachOrdered(report::addEntries);
		return report;
	}

	private static ErrorReport checkField(Class<?> clazz, Field field) {
		String fieldName = field.getName();
		ErrorReport report = ErrorReport.success(String.format("field %s", fieldName));

		Class<?> fieldType = field.getType();
		String fieldTypeName = getName(fieldType);

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

	public static ErrorReport checkModifiers(Class<?> actual, Class<?> spec) {
		return checkModifiers(actual.getModifiers(), spec.getModifiers(), !spec.isInterface() || actual.isInterface());
	}

	public static ErrorReport checkVisibility(Class<?> actual, Class<?> spec) {
		return checkVisibility(actual.getModifiers(), spec.getModifiers());
	}

	public static ErrorReport checkModifiers(Member actual, Member spec) {
		boolean checkAbstract = !spec.getDeclaringClass().isInterface() || actual.getDeclaringClass().isInterface();
		return checkModifiers(actual.getModifiers(), spec.getModifiers(), checkAbstract);
	}

	public static ErrorReport checkVisibility(Member actual, Member spec) {
		return checkVisibility(actual.getModifiers(), spec.getModifiers());
	}

	private static ErrorReport checkModifiers(int actual, int spec, boolean checkAbstract) {
		ErrorReport report = ErrorReport.success();

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

	public static ErrorReport checkVisibility(int actual, int spec) {
		Visibility actualVisibility = getVisibility(actual);
		Visibility specVisibility = getVisibility(spec);

		if (actualVisibility != specVisibility) {
			if (actualVisibility == Visibility.DEFAULT) {
				return ErrorReport.error(String.format("should have visibility \"%s\"", specVisibility));
			}
			return ErrorReport.error(String.format("should have visibility \"%s\" instead of \"%s\"", specVisibility, actualVisibility));
		}

		return ErrorReport.success();
	}

	private static Visibility getVisibility(int modifiers) {
		if (Modifier.isPrivate(modifiers)) {
			return Visibility.PRIVATE;
		} else if (Modifier.isProtected(modifiers)) {
			return Visibility.PROTECTED;
		} else if (Modifier.isPublic(modifiers)) {
			return Visibility.PUBLIC;
		} else {
			return Visibility.DEFAULT;
		}
	}

	private static ErrorReport checkModifier(int actual, int spec, IntFunction<Boolean> function, javax.lang.model.element.Modifier modifier) {
		boolean actualRes = function.apply(actual);
		Boolean specRes = function.apply(spec);

		if (specRes && !actualRes) {
			String format = "should have modifier \"%s\"";
			return ErrorReport.error("", String.format(format, modifier));
		}

		if (!specRes && actualRes) {
			String format = "should not have modifier \"%s\"";
			return ErrorReport.error("", String.format(format, modifier));
		}

		return ErrorReport.success();
	}

	private static String toParameterList(Class<?>[] parameterTypes) {
		return Arrays.stream(parameterTypes).parallel().map(ClassUtils::getName).collect(Collectors.joining(", "));
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
