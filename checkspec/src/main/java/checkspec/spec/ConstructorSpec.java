package checkspec.spec;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConstructorSpec implements Specification<Constructor<?>> {

	@NonNull
	private String name;

	@NonNull
	private ModifiersSpec modifiers;

	@NonNull
	private final VisibilitySpec visibility;

	@NonNull
	private Parameter[] parameters;

	@NonNull
	private Constructor<?> rawElement;

	public static ConstructorSpec from(Constructor<?> constructor) {
		String name = constructor.getName();
		ModifiersSpec modifiers = ModifiersSpec.from(constructor.getModifiers());
		VisibilitySpec visibility = VisibilitySpec.from(constructor.getModifiers(), constructor.getAnnotations());
		Parameter[] parameters = constructor.getParameters();

		return new ConstructorSpec(name, modifiers, visibility, parameters, constructor);
	}
}
