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
