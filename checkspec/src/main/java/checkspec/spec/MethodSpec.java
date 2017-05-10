package checkspec.spec;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MethodSpec implements Spec<Method> {

	@NonNull
	private String name;
	
	@NonNull
	private Class<?> returnType;
	
	@NonNull
	private Parameter[] parameters;
	
	@NonNull
	private ModifiersSpec modifiers;
	
	@NonNull
	private VisibilitySpec visibility;
	
	@NonNull
	private Method rawElement;
	
	public static MethodSpec from(Method method) {
		String name = method.getName();
		Class<?> returnType = method.getReturnType();
		Parameter[] parameterTypes = method.getParameters();
		ModifiersSpec modifiers = ModifiersSpec.from(method.getModifiers());
		VisibilitySpec visibility = VisibilitySpec.from(method.getModifiers(), method.getAnnotations());

		return new MethodSpec(name, returnType, parameterTypes, modifiers, visibility, method);
	}
}
