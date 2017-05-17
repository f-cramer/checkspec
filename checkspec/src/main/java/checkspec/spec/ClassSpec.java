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
public class ClassSpec implements Specification<ResolvableType> {

	@NonNull
	private final String name;

	@NonNull
	private final ModifiersSpec modifiers;

	@NonNull
	private final VisibilitySpec visibility;

	@NonNull
	private final FieldSpec[] declaredFields;

	@NonNull
	private final MethodSpec[] declaredMethods;

	@NonNull
	private final ConstructorSpec[] declaredConstructors;

	@NonNull
	private final ResolvableType rawElement;

	public static ClassSpec from(Class<?> clazz) {
		ResolvableType type = ResolvableType.forClass(clazz);
		
		String name = clazz.getName();
		ModifiersSpec modifiers = ModifiersSpec.from(clazz.getModifiers());
		VisibilitySpec visibility = VisibilitySpec.from(clazz.getModifiers(), clazz.getAnnotations());

		//@formatter:off
		FieldSpec[] declaredFields = Arrays.stream(clazz.getDeclaredFields())
		                                   .parallel()
		                                   .filter(ClassSpec::isIncluded)
		                                   .map(FieldSpec::from)
		                                   .toArray(FieldSpec[]::new);
		//@formatter:on

		//@formatter:off
		MethodSpec[] declaredMethods = Arrays.stream(clazz.getDeclaredMethods())
		                                     .parallel()
		                                     .filter(ClassSpec::isIncluded)
		                                     .map(MethodSpec::from)
		                                     .toArray(MethodSpec[]::new);
		//@formatter:on

		//@formatter:off
		ConstructorSpec[] declaredConstructors = Arrays.stream(clazz.getDeclaredConstructors())
		                                               .parallel()
		                                               .filter(ClassSpec::isIncluded)
		                                               .map(ConstructorSpec::from)
		                                               .toArray(ConstructorSpec[]::new);
		//@formatter:on

		return new ClassSpec(name, modifiers, visibility, declaredFields, declaredMethods, declaredConstructors, type);
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
