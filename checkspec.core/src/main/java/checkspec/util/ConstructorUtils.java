package checkspec.util;

import java.lang.reflect.Constructor;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import checkspec.api.Visibility;
import checkspec.spring.ResolvableType;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class ConstructorUtils {

	public static String createString(@NonNull Constructor<?> constructor) {
		Visibility visibility = getVisibility(constructor);
		String parameterList = getParametersAsString(constructor);
		return String.format("%s <init>(%s)", visibility, parameterList);
	}

	public static Visibility getVisibility(@NonNull Constructor<?> constructor) {
		return MemberUtils.getVisibility(constructor.getModifiers());
	}

	public static ResolvableType[] getParametersAsResolvableType(@NonNull Constructor<?> constructor) {
		return getParameterList(constructor)
				.toArray(ResolvableType[]::new);
	}

	public static String getParametersAsString(@NonNull Constructor<?> constructor) {
		return getParameterList(constructor)
				.map(ClassUtils::getName)
				.collect(Collectors.joining(", "));
	}

	private static Stream<ResolvableType> getParameterList(Constructor<?> constructor) {
		return IntStream.range(0, constructor.getParameterCount()).parallel()
				.mapToObj(i -> ResolvableType.forConstructorParameter(constructor, i));
	}
}
