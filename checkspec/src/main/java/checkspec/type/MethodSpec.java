package checkspec.type;

import java.lang.reflect.Method;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MethodSpec implements MemberSpec<Method> {

	@NonNull
	private String name;
	
	@NonNull
	private Class<?> returnType;
	
	@NonNull
	private Class<?>[] parameterTypes;
	
	@NonNull
	private ModifiersSpec modifiers;
	
	@NonNull
	private Method rawElement;
	
	public static MethodSpec from(Method method) {
		String name = method.getName();
		Class<?> returnType = method.getReturnType();
		Class<?>[] parameterTypes = method.getParameterTypes();
		ModifiersSpec modifiers = ModifiersSpec.from(method.getModifiers());

		return new MethodSpec(name, returnType, parameterTypes, modifiers, method);
	}
}
