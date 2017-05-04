package checkspec.util;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import checkspec.type.Visibility;

public class ConstructorUtils {

	public static String createString(Constructor<?> constructor) {
		return toString(constructor);
	}

	public static String toString(Constructor<?> constructor) {
		Visibility visibility = getVisibility(constructor);
		String parameterList = getParameterList(constructor);
		return String.format("%s <init>(%s)", visibility, parameterList);
	}

	public static Visibility getVisibility(Constructor<?> constructor) {
		Objects.requireNonNull(constructor);
		return MemberUtils.getVisibility(constructor.getModifiers());
	}

	public static String getParameterList(Constructor<?> constructor) {
		Objects.requireNonNull(constructor);
		return Arrays.stream(constructor.getParameterTypes()).parallel().map(ClassUtils::getName).collect(Collectors.joining(", "));
	}
}
