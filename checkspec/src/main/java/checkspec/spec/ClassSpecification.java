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
	private final FieldSpecification[] declaredFields;

	@NonNull
	private final MethodSpecification[] declaredMethods;

	@NonNull
	private final ConstructorSpecification[] declaredConstructors;

	@NonNull
	private final ResolvableType rawElement;

	public static ClassSpecification from(Class<?> clazz) {
		ResolvableType type = ResolvableType.forClass(clazz);
		
		String name = clazz.getName();
		ModifiersSpecification modifiers = ModifiersSpecification.from(clazz.getModifiers(), clazz.getAnnotations());
		VisibilitySpecification visibility = VisibilitySpecification.from(clazz.getModifiers(), clazz.getAnnotations());

		//@formatter:off
		FieldSpecification[] declaredFields = Arrays.stream(clazz.getDeclaredFields())
		                                            .parallel()
		                                            .filter(ClassSpecification::isIncluded)
		                                            .map(FieldSpecification::from)
		                                            .toArray(FieldSpecification[]::new);
		//@formatter:on

		//@formatter:off
		MethodSpecification[] declaredMethods = Arrays.stream(clazz.getDeclaredMethods())
		                                              .parallel()
		                                              .filter(ClassSpecification::isIncluded)
		                                              .map(MethodSpecification::from)
		                                              .toArray(MethodSpecification[]::new);
		//@formatter:on

		//@formatter:off
		ConstructorSpecification[] declaredConstructors = Arrays.stream(clazz.getDeclaredConstructors())
		                                                        .parallel()
		                                                        .filter(ClassSpecification::isIncluded)
		                                                        .map(ConstructorSpecification::from)
		                                                        .toArray(ConstructorSpecification[]::new);
		//@formatter:on

		return new ClassSpecification(name, modifiers, visibility, declaredFields, declaredMethods, declaredConstructors, type);
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
