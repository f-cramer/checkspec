package checkspec.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import checkspec.type.Visibility;

public class MethodUtils {

	public static String createString(Method method) {
		return toString(method);
	}

	public static String toString(Method method) {
		Visibility visibility = getVisibility(method);
		String returnTypeName = getReturnTypeName(method);
		String parameterList = getParameterList(method);
		return String.format("%s %s %s(%s)", visibility, returnTypeName, method.getName(), parameterList);
	}

	public static Visibility getVisibility(Method method) {
		Objects.requireNonNull(method);
		return MemberUtils.getVisibility(method.getModifiers());
	}

	public static String getReturnTypeName(Method method) {
		Objects.requireNonNull(method);
		return ClassUtils.getName(method.getReturnType());
	}

	public static String getParameterList(Method method) {
		Objects.requireNonNull(method);
		return Arrays.stream(method.getParameterTypes()).parallel().map(ClassUtils::getName).collect(Collectors.joining(", "));
	}

	public static boolean isAbstract(Method method) {
		return Modifier.isAbstract(method.getModifiers());
	}
}
