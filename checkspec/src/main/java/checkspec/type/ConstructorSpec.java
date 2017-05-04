package checkspec.type;

import java.lang.reflect.Constructor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConstructorSpec implements MemberSpec<Constructor<?>> {

	@NonNull
	private String name;
	
	@NonNull
	private Class<?> declaringClass;
	
	@NonNull
	private ModifiersSpec modifiers;
	
	@NonNull
	private Constructor<?> rawElement;
	
	public static ConstructorSpec from(Constructor<?> constructor) {
		String name = constructor.getName();
		Class<?> declaringClass = constructor.getDeclaringClass();
		ModifiersSpec modifiers = ModifiersSpec.from(constructor.getModifiers());

		return new ConstructorSpec(name, declaringClass, modifiers, constructor);
	}
}
