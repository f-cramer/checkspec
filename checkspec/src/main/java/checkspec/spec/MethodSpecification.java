package checkspec.spec;

import java.lang.reflect.Method;
import java.util.stream.IntStream;

import checkspec.spring.ResolvableType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MethodSpecification implements Specification<Method> {

	@NonNull
	private final String name;

	@NonNull
	private final ResolvableType returnType;

	@NonNull
	private final MethodParameterSpecification[] parameters;

	@NonNull
	private final ModifiersSpecification modifiers;

	@NonNull
	private final VisibilitySpecification visibility;

	@NonNull
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
