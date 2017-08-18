package checkspec.specification;

import java.lang.reflect.Constructor;
import java.util.List;

import checkspec.extension.AbstractExtendable;
import checkspec.type.MatchableType;
import checkspec.util.TypeDiscovery;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
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

		parameters = new ParametersSpecification(constructor.getParameters(), index -> MatchableType.forConstructorParameter(constructor, index));

		performExtensions(EXTENSIONS, this, constructor);
	}

	@Override
	public int compareTo(ConstructorSpecification other) {
		List<ParameterSpecification> parameterSpecifications = parameters.getParameterSpecifications();
		List<ParameterSpecification> otherParameterSpecifications = other.parameters.getParameterSpecifications();
		int length = Math.min(parameterSpecifications.size(), otherParameterSpecifications.size());
		for (int i = 0; i < length; i++) {
			Class<?> thisClass = parameterSpecifications.get(i).getType().getRawClass();
			Class<?> otherClass = otherParameterSpecifications.get(i).getType().getRawClass();

			if (thisClass != otherClass) {
				return thisClass.getName().compareTo(otherClass.getName());
			}
		}

		return Integer.compare(parameterSpecifications.size(), otherParameterSpecifications.size());
	}
}
