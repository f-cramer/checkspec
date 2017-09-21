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



import java.util.List;

import checkspec.extension.AbstractExtendable;
import checkspec.type.MatchableType;
import checkspec.util.TypeDiscovery;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

/**
 * A specification for an exception that is thrown by a constructor or a method.
 *
 * @author Florian Cramer
 *
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class ExceptionSpecification extends AbstractExtendable<ExceptionSpecification, MatchableType> implements Specification<MatchableType> {

	private static final ExceptionSpecificationExtension[] EXTENSIONS;

	static {
		List<ExceptionSpecificationExtension> instances = TypeDiscovery.getUniqueInstancesOf(ExceptionSpecificationExtension.class);
		EXTENSIONS = instances.toArray(new ExceptionSpecificationExtension[instances.size()]);
	}

	private final String name;
	private final MatchableType rawElement;

	/**
	 * Constructs a new {@link ExceptionSpecification} from the given throwable
	 * type.
	 *
	 * @param throwable
	 *            the throwable type
	 */
	public ExceptionSpecification(@NonNull MatchableType throwable) {
		this.name = throwable.toString();
		this.rawElement = throwable;

		performExtensions(EXTENSIONS, this, rawElement);
	}
}
