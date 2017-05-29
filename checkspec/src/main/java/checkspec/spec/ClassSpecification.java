package checkspec.spec;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.annotation.Nonnull;

import checkspec.api.Spec;
import checkspec.spring.ResolvableType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassSpecification implements Specification<ResolvableType> {

	@Nonnull
	private final String name;

	@Nonnull
	private final ModifiersSpecification modifiers;

	@Nonnull
	private final VisibilitySpecification visibility;

	@Nonnull
	private final FieldSpecification[] declaredFields;

	@Nonnull
	private final MethodSpecification[] declaredMethods;

	@Nonnull
	private final ConstructorSpecification[] declaredConstructors;

	@Nonnull
	private final ResolvableType rawElement;

	public ClassSpecification(Class<?> clazz) {
		rawElement = ResolvableType.forClass(clazz);

		name = clazz.getName();
		modifiers = new ModifiersSpecification(clazz.getModifiers(), clazz.getAnnotations());
		visibility = new VisibilitySpecification(clazz.getModifiers(), clazz.getAnnotations());

		//@formatter:off
		declaredFields = Arrays.stream(clazz.getDeclaredFields())
		                                    .parallel()
		                                    .filter(ClassSpecification::isIncluded)
		                                    .map(FieldSpecification::new)
		                                    .toArray(FieldSpecification[]::new);
		//@formatter:on

		//@formatter:off
		declaredMethods = Arrays.stream(clazz.getDeclaredMethods())
		                                     .parallel()
		                                     .filter(ClassSpecification::isIncluded)
		                                     .map(MethodSpecification::new)
		                                     .toArray(MethodSpecification[]::new);
		//@formatter:on

		//@formatter:off
		declaredConstructors = Arrays.stream(clazz.getDeclaredConstructors())
		                                          .parallel()
		                                          .filter(ClassSpecification::isIncluded)
		                                          .map(ConstructorSpecification::new)
		                                          .toArray(ConstructorSpecification[]::new);
		//@formatter:on
	}

	private static boolean isIncluded(Field field) {
		return isIncluded(field.getAnnotation(Spec.class));
	}

	private static boolean isIncluded(Method method) {
		return isIncluded(method.getAnnotation(Spec.class));
	}

	private static boolean isIncluded(Constructor<?> constructor) {
		return isIncluded(constructor.getAnnotation(Spec.class));
	}

	private static boolean isIncluded(Spec spec) {
		return spec == null || spec.value();
	}
}
