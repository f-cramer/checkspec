package checkspec.spec;

import java.lang.reflect.Method;
import java.util.stream.IntStream;

import checkspec.spring.MethodParameter;
import checkspec.spring.ResolvableType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MethodSpec implements Specification<Method> {

	@NonNull
	private String name;

	@NonNull
	private ResolvableType returnType;

	@NonNull
	private MethodParameter[] parameters;

	@NonNull
	private ModifiersSpec modifiers;

	@NonNull
	private VisibilitySpec visibility;

	@NonNull
	private Method rawElement;

	public static MethodSpec from(Method method) {

		String name = method.getName();
		ResolvableType returnType = ResolvableType.forMethodReturnType(method);

		//@formatter:off
		MethodParameter[] parameters = IntStream.range(0, method.getParameterCount())
		                                        .mapToObj(i -> ResolvableType.forMethodParameter(method, i))
		                                        .toArray(MethodParameter[]::new);
		//@formatter:on

		ModifiersSpec modifiers = ModifiersSpec.from(method.getModifiers());
		VisibilitySpec visibility = VisibilitySpec.from(method.getModifiers(), method.getAnnotations());

		return new MethodSpec(name, returnType, parameters, modifiers, visibility, method);
	}
}
