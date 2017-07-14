package checkspec.specification;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import checkspec.api.Spec;
import checkspec.extension.AbstractExtendable;
import checkspec.type.ResolvableType;
import checkspec.util.TypeDiscovery;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
public class ClassSpecification extends AbstractExtendable<ClassSpecification, ResolvableType> implements Specification<ResolvableType> {

	private static final ClassSpecificationExtension[] EXTENSIONS;

	static {
		List<ClassSpecificationExtension> instances = TypeDiscovery.getUniqueInstancesOf(ClassSpecificationExtension.class);
		EXTENSIONS = instances.toArray(new ClassSpecificationExtension[instances.size()]);
	}

	@NonNull
	private final String name;

	@NonNull
	@Getter(AccessLevel.NONE)
	private final PackageSpecification pkg;

	@NonNull
	private final ModifiersSpecification modifiers;

	@NonNull
	private final VisibilitySpecification visibility;

	@NonNull
	private final SuperclassSpecification superclassSpecification;

	@NonNull
	private final InterfaceSpecification[] interfaceSpecifications;

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
		pkg = new PackageSpecification(clazz.getPackage(), clazz.getAnnotations());
		modifiers = new ModifiersSpecification(clazz.getModifiers(), clazz.getAnnotations());
		visibility = new VisibilitySpecification(clazz.getModifiers(), clazz.getAnnotations());
		superclassSpecification = new SuperclassSpecification(rawElement.getSuperType());

		interfaceSpecifications = Arrays.stream(rawElement.getInterfaces()).parallel()
				.map(InterfaceSpecification::new)
				.toArray(InterfaceSpecification[]::new);

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

		performExtensions(EXTENSIONS, this, rawElement);
	}

	public PackageSpecification getPackage() {
		return pkg;
	}

	public static boolean shouldBeGenerated(Class<?> clazz) {
		return isIncluded(clazz.getAnnotation(Spec.class));
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
