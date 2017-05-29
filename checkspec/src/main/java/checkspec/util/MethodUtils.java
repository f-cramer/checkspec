package checkspec.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;

import checkspec.api.Visibility;
import checkspec.spring.ResolvableType;

public class MethodUtils {

	public static String createString(@Nonnull Method method) {
		return toString(method);
	}

	public static String toString(@Nonnull Method method) {
		Visibility visibility = getVisibility(method);
		String returnTypeName = getReturnTypeName(method);
		String parameterList = getParameterList(method);
		return String.format("%s %s %s(%s)", visibility, returnTypeName, method.getName(), parameterList);
	}

	public static Visibility getVisibility(@Nonnull Method method) {
		return MemberUtils.getVisibility(method.getModifiers());
	}

	public static String getReturnTypeName(@Nonnull Method method) {
		return ClassUtils.getName(ResolvableType.forMethodReturnType(method));
	}

	public static String getParameterList(@Nonnull Method method) {
		//@formatter:off
		return IntStream.range(0, method.getParameterCount())
		                .parallel()
		                .mapToObj(i -> ResolvableType.forMethodParameter(method, i))
		                .map(ClassUtils::getName)
		                .collect(Collectors.joining(", "));
		//@formatter:on
	}

	public static boolean isAbstract(@Nonnull Method method) {
		return Modifier.isAbstract(method.getModifiers());
	}
}
