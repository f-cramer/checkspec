package checkspec.spec;

import java.lang.reflect.Constructor;

import checkspec.spring.ResolvableType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConstructorSpecification implements Specification<Constructor<?>>, Comparable<ConstructorSpecification> {

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
	}

	@Override
	public int compareTo(ConstructorSpecification o) {
		ParameterSpecification[] parameterSpecifications = parameters.getParameterSpecifications();
		ParameterSpecification[] oParameterSpecifications = o.parameters.getParameterSpecifications();
		int length = Math.min(parameterSpecifications.length, oParameterSpecifications.length);
		for (int i = 0; i < length; i++) {
			Class<?> thisClass = parameterSpecifications[i].getType().getRawClass();
			Class<?> oClass = oParameterSpecifications[i].getType().getRawClass();

			if (thisClass != oClass) {
				return thisClass.getName().compareTo(oClass.getName());
			}
		}

		return Integer.compare(parameterSpecifications.length, oParameterSpecifications.length);
	}
}
