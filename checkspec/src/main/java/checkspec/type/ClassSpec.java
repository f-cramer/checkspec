package checkspec.type;

import java.util.Arrays;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassSpec implements Spec<Class<?>> {

	@NonNull
	private final String name;
	
	@NonNull
	private final ModifiersSpec modifiers;
	
	@NonNull
	private final FieldSpec[] declaredFields;
	
	@NonNull
	private final MethodSpec[] declaredMethods;
	
	@NonNull
	private final ConstructorSpec[] declaredConstructors;
	
	@NonNull
	private final Class<?> rawElement;
	
	public static ClassSpec from(Class<?> clazz) {
		String name = clazz.getName();
		ModifiersSpec modifiers = ModifiersSpec.from(clazz.getModifiers());
		FieldSpec[] declaredFields = Arrays.stream(clazz.getDeclaredFields()).parallel().map(FieldSpec::from).toArray(FieldSpec[]::new);
		MethodSpec[] declaredMethods = Arrays.stream(clazz.getDeclaredMethods()).parallel().map(MethodSpec::from).toArray(MethodSpec[]::new);
		ConstructorSpec[] declaredConstructors = Arrays.stream(clazz.getDeclaredConstructors()).parallel().map(ConstructorSpec::from).toArray(ConstructorSpec[]::new);

		return new ClassSpec(name, modifiers, declaredFields, declaredMethods, declaredConstructors, clazz);
	}
}
