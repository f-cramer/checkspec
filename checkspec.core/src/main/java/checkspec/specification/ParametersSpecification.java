package checkspec.specification;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import checkspec.type.MatchableType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

@Value
@EqualsAndHashCode
public class ParametersSpecification implements Specification<List<Parameter>> {

	@Getter(AccessLevel.PACKAGE)
	private final List<ParameterSpecification> parameterSpecifications;

	public ParametersSpecification(Parameter[] parameters, IntFunction<MatchableType> typeGenerator) {
		parameterSpecifications = IntStream.range(0, parameters.length)
				.mapToObj(i -> new ParameterSpecification(parameters[i], typeGenerator.apply(i)))
				.collect(Collectors.toList());
	}

	@Override
	public String getName() {
		return parameterSpecifications.parallelStream()
				.map(ParameterSpecification::getName)
				.collect(Collectors.joining("(", ", ", ")"));
	}

	@Override
	public List<Parameter> getRawElement() {
		return parameterSpecifications.parallelStream()
				.map(ParameterSpecification::getRawElement)
				.collect(Collectors.toList());
	}

	public int getCount() {
		return parameterSpecifications.size();
	}

	public ParameterSpecification get(int index) {
		return parameterSpecifications.get(index);
	}
}
