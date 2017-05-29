package checkspec.spec;

import java.lang.reflect.Constructor;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConstructorSpecification implements Specification<Constructor<?>> {

	@Nonnull
	private final String name;

	@Nonnull
	private final ModifiersSpecification modifiers;

	@Nonnull
	private final VisibilitySpecification visibility;

	@Nonnull
	private final MethodParameterSpecification[] parameters;

	@Nonnull
	private final Constructor<?> rawElement;

	public ConstructorSpecification(Constructor<?> constructor) {
		name = constructor.getName();
		modifiers = new ModifiersSpecification(constructor.getModifiers(), constructor.getAnnotations());
		visibility = new VisibilitySpecification(constructor.getModifiers(), constructor.getAnnotations());
		rawElement = constructor;

		//@formatter:off
		parameters = IntStream.range(0, constructor.getParameterCount())
		                      .mapToObj(i -> new MethodParameterSpecification(constructor, i))
		                      .toArray(MethodParameterSpecification[]::new);
		//@formatter:on
	}
}
