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
	private String name;

	@NonNull
	private ResolvableType returnType;

	@NonNull
	private MethodParameterSpecification[] parameters;

	@NonNull
	private ModifiersSpecification modifiers;

	@NonNull
	private VisibilitySpecification visibility;

	@NonNull
	private Method rawElement;

	public static MethodSpecification from(Method method) {

		String name = method.getName();
		ResolvableType returnType = ResolvableType.forMethodReturnType(method);

		//@formatter:off
		MethodParameterSpecification[] parameters = IntStream.range(0, method.getParameterCount())
		                                            .mapToObj(i -> MethodParameterSpecification.from(method, i))
		                                            .toArray(MethodParameterSpecification[]::new);
		//@formatter:on

		ModifiersSpecification modifiers = ModifiersSpecification.from(method.getModifiers(), method.getAnnotations());
		VisibilitySpecification visibility = VisibilitySpecification.from(method.getModifiers(), method.getAnnotations());

		return new MethodSpecification(name, returnType, parameters, modifiers, visibility, method);
	}
}
