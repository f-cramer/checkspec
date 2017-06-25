package checkspec.spec;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import checkspec.api.Visibility;
import checkspec.spring.ResolvableType;
import lombok.AccessLevel;
import lombok.Getter;

@Getter
public class ParametersSpecification implements Specification<Parameter[]> {

	@Getter(AccessLevel.PACKAGE)
	private final ParameterSpecification[] parameterSpecifications;

	private final ModifiersSpecification modifiers = new ModifiersSpecification(0, new Annotation[0]);
	private final VisibilitySpecification visibility = new VisibilitySpecification(new Visibility[0]);

	public ParametersSpecification(Parameter[] parameters, IntFunction<ResolvableType> typeGenerator) {
		parameterSpecifications = IntStream.range(0, parameters.length)
				.mapToObj(i -> new ParameterSpecification(parameters[i], typeGenerator.apply(i)))
				.toArray(ParameterSpecification[]::new);
	}

	@Override
	public String getName() {
		return Arrays.stream(parameterSpecifications).parallel()
				.map(ParameterSpecification::getName)
				.collect(Collectors.joining("(", ", ", ")"));
	}

	@Override
	public Parameter[] getRawElement() {
		return Arrays.stream(parameterSpecifications).parallel()
				.map(ParameterSpecification::getRawElement)
				.toArray(Parameter[]::new);
	}

	public int getCount() {
		return parameterSpecifications.length;
	}

	public ParameterSpecification get(int index) {
		return parameterSpecifications[index];
	}
}
