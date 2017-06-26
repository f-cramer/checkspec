package checkspec.spec;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import checkspec.extension.MethodSpecificationExtension;
import checkspec.spring.ResolvableType;
import checkspec.util.TypeDiscovery;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MethodSpecification extends AbstractExtendable implements ExecutableSpecification<Method>, Comparable<MethodSpecification> {

	private static final MethodSpecificationExtension[] EXTENSIONS;

	static {
		List<MethodSpecificationExtension> instances = TypeDiscovery.getInstancesOf(MethodSpecificationExtension.class);
		EXTENSIONS = instances.toArray(new MethodSpecificationExtension[instances.size()]);
	}

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

		performExtensions(EXTENSIONS, method, this);
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
