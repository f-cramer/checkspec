package checkspec.spec;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Objects;

import checkspec.spring.ResolvableType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MethodSpecification implements Specification<Method>, Comparable<MethodSpecification> {

	@NonNull
	private final String name;

	@NonNull
	private final ResolvableType returnType;

	@NonNull
	private final ParametersSpecification parameters;

	@NonNull
	private final ModifiersSpecification modifiers;

	@NonNull
	private final VisibilitySpecification visibility;

	@NonNull
	private final Method rawElement;

	public MethodSpecification(Method method) {
		name = method.getName();
		returnType = ResolvableType.forMethodReturnType(method);

		parameters = new ParametersSpecification(method.getParameters(), index -> ResolvableType.forMethodParameter(method, index));
		modifiers = new ModifiersSpecification(method.getModifiers(), method.getAnnotations());
		visibility = new VisibilitySpecification(method.getModifiers(), method.getAnnotations());
		rawElement = method;
	}

	@Override
	public int compareTo(MethodSpecification o) {
		int nameComp = Objects.compare(name, o.name, Comparator.naturalOrder());
		if (nameComp != 0) {
			return nameComp;
		}

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
