package checkspec.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import checkspec.api.Visibility;
import checkspec.spring.ResolvableType;
import lombok.NonNull;

public class MethodUtils {

	public static String createString(@NonNull Method method) {
		return toString(method);
	}

	public static String toString(@NonNull Method method) {
		Visibility visibility = getVisibility(method);
		String returnTypeName = getReturnTypeName(method);
		String parameterList = getParameterList(method);
		return String.format("%s %s %s(%s)", visibility, returnTypeName, method.getName(), parameterList);
	}

	public static Visibility getVisibility(@NonNull Method method) {
		return MemberUtils.getVisibility(method.getModifiers());
	}

	public static String getReturnTypeName(@NonNull Method method) {
		return ClassUtils.getName(ResolvableType.forMethodReturnType(method));
	}

	public static String getParameterList(@NonNull Method method) {
		return IntStream.range(0, method.getParameterCount()).parallel()
				.mapToObj(i -> ResolvableType.forMethodParameter(method, i))
				.map(ClassUtils::getName)
				.collect(Collectors.joining(", "));
	}

	public static boolean isAbstract(@NonNull Method method) {
		return Modifier.isAbstract(method.getModifiers());
	}
}
