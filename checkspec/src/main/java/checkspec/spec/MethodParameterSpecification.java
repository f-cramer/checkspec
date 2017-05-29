package checkspec.spec;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.annotation.Nonnull;

import checkspec.spring.ResolvableType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MethodParameterSpecification {

	@Nonnull
	private final String name;

	@Nonnull
	private final ResolvableType type;

	@Nonnull
	private final Parameter rawElement;
	
	public MethodParameterSpecification(Method method, int parameterIndex) {
		type = ResolvableType.forMethodParameter(method, parameterIndex);
		rawElement = method.getParameters()[parameterIndex];
		name = rawElement.getName();
	}

	public MethodParameterSpecification(Constructor<?> constructor, int parameterIndex) {
		type = ResolvableType.forConstructorParameter(constructor, parameterIndex);
		rawElement = constructor.getParameters()[parameterIndex];
		name = rawElement.getName();
	}}
