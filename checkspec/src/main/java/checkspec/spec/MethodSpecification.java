package checkspec.spec;

import java.lang.reflect.Method;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;

import checkspec.spring.ResolvableType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MethodSpecification implements Specification<Method> {

	@Nonnull
	private final String name;

	@Nonnull
	private final ResolvableType returnType;

	@Nonnull
	private final MethodParameterSpecification[] parameters;

	@Nonnull
	private final ModifiersSpecification modifiers;

	@Nonnull
	private final VisibilitySpecification visibility;

	@Nonnull
	private final Method rawElement;

	public MethodSpecification(Method method) {
		name = method.getName();
		returnType = ResolvableType.forMethodReturnType(method);

		//@formatter:off
		parameters = IntStream.range(0, method.getParameterCount())
		                      .mapToObj(i -> new MethodParameterSpecification(method, i))
		                      .toArray(MethodParameterSpecification[]::new);
		//@formatter:on

		modifiers = new ModifiersSpecification(method.getModifiers(), method.getAnnotations());
		visibility = new VisibilitySpecification(method.getModifiers(), method.getAnnotations());
		rawElement = method;
	}
}
