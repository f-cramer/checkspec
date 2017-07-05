package checkspec.specification;

import java.lang.reflect.Constructor;
import java.util.List;

import checkspec.extension.AbstractExtendable;
import checkspec.spring.ResolvableType;
import checkspec.util.TypeDiscovery;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConstructorSpecification extends AbstractExtendable<ConstructorSpecification, Constructor<?>> implements ExecutableSpecification<Constructor<?>>, Comparable<ConstructorSpecification> {

	private static final ConstructorSpecificationExtension[] EXTENSIONS;

	static {
		List<ConstructorSpecificationExtension> instances = TypeDiscovery.getUniqueInstancesOf(ConstructorSpecificationExtension.class);
		EXTENSIONS = instances.toArray(new ConstructorSpecificationExtension[instances.size()]);
	}

	@NonNull
	private final String name;

	@NonNull
	private final ModifiersSpecification modifiers;

	@NonNull
	private final VisibilitySpecification visibility;

	@NonNull
	private final ParametersSpecification parameters;

	@NonNull
	private final Constructor<?> rawElement;

	public ConstructorSpecification(Constructor<?> constructor) {
		name = constructor.getName();
		modifiers = new ModifiersSpecification(constructor.getModifiers(), constructor.getAnnotations());
		visibility = new VisibilitySpecification(constructor.getModifiers(), constructor.getAnnotations());
		rawElement = constructor;

		parameters = new ParametersSpecification(constructor.getParameters(), index -> ResolvableType.forConstructorParameter(constructor, index));

		performExtensions(EXTENSIONS, this, constructor);
	}

	@Override
	public int compareTo(ConstructorSpecification other) {
		ParameterSpecification[] parameterSpecifications = parameters.getParameterSpecifications();
		ParameterSpecification[] otherParameterSpecifications = other.parameters.getParameterSpecifications();
		int length = Math.min(parameterSpecifications.length, otherParameterSpecifications.length);
		for (int i = 0; i < length; i++) {
			Class<?> thisClass = parameterSpecifications[i].getType().getRawClass();
			Class<?> otherClass = otherParameterSpecifications[i].getType().getRawClass();

			if (thisClass != otherClass) {
				return thisClass.getName().compareTo(otherClass.getName());
			}
		}

		return Integer.compare(parameterSpecifications.length, otherParameterSpecifications.length);
	}
}
