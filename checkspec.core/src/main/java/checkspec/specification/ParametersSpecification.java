package checkspec.specification;

/*-
 * #%L
 * CheckSpec Core
 * %%
 * Copyright (C) 2017 Florian Cramer
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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

/**
 * A specification for the parameters of a constructor or method.
 *
 * @author Florian Cramer
 *
 */
@Value
@EqualsAndHashCode
public class ParametersSpecification implements Specification<List<Parameter>> {

	@Getter(AccessLevel.PACKAGE)
	private final List<ParameterSpecification> parameters;

	/**
	 * Creates a new {@link ParametersSpecification} from the given parameters
	 * and type generator.
	 *
	 * @param parameters
	 *            the parameters
	 * @param typeGenerator
	 *            the type generator
	 */
	public ParametersSpecification(Parameter[] parameters, IntFunction<MatchableType> typeGenerator) {
		this.parameters = IntStream.range(0, parameters.length)
				.mapToObj(i -> new ParameterSpecification(parameters[i], typeGenerator.apply(i)))
				.collect(Collectors.toList());
	}

	@Override
	public String getName() {
		return parameters.parallelStream()
				.map(ParameterSpecification::getName)
				.collect(Collectors.joining("(", ", ", ")"));
	}

	@Override
	public List<Parameter> getRawElement() {
		return parameters.parallelStream()
				.map(ParameterSpecification::getRawElement)
				.collect(Collectors.toList());
	}

	/**
	 * Returns the number of parameters in this specification.
	 *
	 * @return the number of parameters
	 */
	public int getCount() {
		return parameters.size();
	}

	/**
	 * Returns the parameter at the given {@code index}.
	 *
	 * @param index
	 *            the index
	 * @return the parameter at the given index
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range {@code (index < 0 || index >=
	 *             getCount())}
	 */
	public ParameterSpecification get(int index) {
		return parameters.get(index);
	}
}
