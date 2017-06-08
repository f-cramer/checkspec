package checkspec.spec;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import checkspec.api.Spec;
import checkspec.spring.ResolvableType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassSpecification implements Specification<ResolvableType> {

	@NonNull
	private final String name;

	@NonNull
	private final ModifiersSpecification modifiers;

	@NonNull
	private final VisibilitySpecification visibility;

	@NonNull
	private final FieldSpecification[] fieldSpecifications;

	@NonNull
	private final MethodSpecification[] methodSpecifications;

	@NonNull
	private final ConstructorSpecification[] constructorSpecifications;

	@NonNull
	private final ResolvableType rawElement;

	public ClassSpecification(Class<?> clazz) {
		rawElement = ResolvableType.forClass(clazz);

		name = clazz.getName();
		modifiers = new ModifiersSpecification(clazz.getModifiers(), clazz.getAnnotations());
		visibility = new VisibilitySpecification(clazz.getModifiers(), clazz.getAnnotations());

		fieldSpecifications = Arrays.stream(clazz.getDeclaredFields()).parallel()
				.filter(ClassSpecification::isIncluded)
				.map(FieldSpecification::new)
				.toArray(FieldSpecification[]::new);

		methodSpecifications = Arrays.stream(clazz.getDeclaredMethods()).parallel()
				.filter(ClassSpecification::isIncluded)
				.map(MethodSpecification::new)
				.toArray(MethodSpecification[]::new);

		constructorSpecifications = Arrays.stream(clazz.getDeclaredConstructors()).parallel()
				.filter(ClassSpecification::isIncluded)
				.map(ConstructorSpecification::new)
				.toArray(ConstructorSpecification[]::new);
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
