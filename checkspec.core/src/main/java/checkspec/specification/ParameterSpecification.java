package checkspec.specification;

import java.lang.reflect.Parameter;

import checkspec.type.MatchableType;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@Value
@EqualsAndHashCode()
public class ParameterSpecification implements Specification<Parameter> {

	@NonNull
	private final String name;

	@NonNull
	private final MatchableType type;

	@NonNull
	private final ModifiersSpecification modifiers;

	@NonNull
	private final Parameter rawElement;

	public ParameterSpecification(Parameter parameter, MatchableType type) {
		this.type = type;
		rawElement = parameter;
		modifiers = new ModifiersSpecification(parameter.getModifiers(), parameter.getAnnotations());
		name = parameter.getName();
	}
}
