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

import checkspec.type.MatchableType;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

/**
 * Specification for a single parameter of a constructor or a method.
 *
 * @author Florian Cramer
 *
 */
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

	/**
	 * Creates a new {@link ParameterSpecification} from the given parameter and
	 * type.
	 *
	 * @param parameter
	 *            the parameter
	 * @param type
	 *            the type
	 */
	public ParameterSpecification(Parameter parameter, MatchableType type) {
		this.type = type;
		rawElement = parameter;
		modifiers = new ModifiersSpecification(parameter.getModifiers(), parameter.getAnnotations());
		name = parameter.getName();
	}
}
