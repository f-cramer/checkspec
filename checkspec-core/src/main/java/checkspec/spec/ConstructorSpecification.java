package checkspec.spec;

import java.lang.reflect.Constructor;
import java.util.stream.IntStream;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConstructorSpecification implements Specification<Constructor<?>> {

	@NonNull
	private final String name;

	@NonNull
	private final ModifiersSpecification modifiers;

	@NonNull
	private final VisibilitySpecification visibility;

	@NonNull
	private final MethodParameterSpecification[] parameters;

	@NonNull
	private final Constructor<?> rawElement;

	public ConstructorSpecification(Constructor<?> constructor) {
		name = constructor.getName();
		modifiers = new ModifiersSpecification(constructor.getModifiers(), constructor.getAnnotations());
		visibility = new VisibilitySpecification(constructor.getModifiers(), constructor.getAnnotations());
		rawElement = constructor;

		parameters = IntStream.range(0, constructor.getParameterCount())
				.mapToObj(i -> new MethodParameterSpecification(constructor, i))
				.toArray(MethodParameterSpecification[]::new);
	}
}
