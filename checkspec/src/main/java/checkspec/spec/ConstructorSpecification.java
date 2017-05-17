package checkspec.spec;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConstructorSpecification implements Specification<Constructor<?>> {

	@NonNull
	private String name;

	@NonNull
	private ModifiersSpecification modifiers;

	@NonNull
	private final VisibilitySpecification visibility;

	@NonNull
	private Parameter[] parameters;

	@NonNull
	private Constructor<?> rawElement;

	public static ConstructorSpecification from(Constructor<?> constructor) {
		String name = constructor.getName();
		ModifiersSpecification modifiers = ModifiersSpecification.from(constructor.getModifiers(), constructor.getAnnotations());
		VisibilitySpecification visibility = VisibilitySpecification.from(constructor.getModifiers(), constructor.getAnnotations());
		Parameter[] parameters = constructor.getParameters();

		return new ConstructorSpecification(name, modifiers, visibility, parameters, constructor);
	}
}
