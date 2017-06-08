package checkspec.util;

import java.lang.reflect.Constructor;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import checkspec.api.Visibility;
import checkspec.spring.ResolvableType;
import lombok.NonNull;

public class ConstructorUtils {

	public static String createString(Constructor<?> constructor) {
		return toString(constructor);
	}

	public static String toString(Constructor<?> constructor) {
		Visibility visibility = getVisibility(constructor);
		String parameterList = getParameterList(constructor);
		return String.format("%s <init>(%s)", visibility, parameterList);
	}

	public static Visibility getVisibility(@NonNull Constructor<?> constructor) {
		return MemberUtils.getVisibility(constructor.getModifiers());
	}

	public static String getParameterList(@NonNull Constructor<?> constructor) {
		//@formatter:off
		return IntStream.range(0, constructor.getParameterCount())
		                .parallel()
		                .mapToObj(i -> ResolvableType.forConstructorParameter(constructor, i))
		                .map(ClassUtils::getName)
		                .collect(Collectors.joining(", "));
		//@formatter:on
	}
}
