package checkspec.spec;

import java.lang.reflect.Parameter;

import checkspec.api.Visibility;
import checkspec.spring.ResolvableType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ParameterSpecification implements Specification<Parameter> {

	@NonNull
	private final String name;

	@NonNull
	private final ResolvableType type;

	@NonNull
	private final ModifiersSpecification modifiers;

	@NonNull
	private final VisibilitySpecification visibility = new VisibilitySpecification(new Visibility[0]);

	@NonNull
	private final Parameter rawElement;

	public ParameterSpecification(Parameter parameter, ResolvableType type) {
		this.type = type;
		rawElement = parameter;
		modifiers = new ModifiersSpecification(parameter.getModifiers(), parameter.getAnnotations());
		name = parameter.getName();
	}
}
