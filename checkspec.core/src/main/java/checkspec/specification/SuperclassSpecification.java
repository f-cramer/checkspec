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
import checkspec.util.ClassUtils;
import checkspec.util.TypeDiscovery;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * A specification for a superclass of a class.
 *
 * @author Florian Cramer
 *
 */
@Getter
@ToString
public class SuperclassSpecification extends AbstractExtendable<SuperclassSpecification, MatchableType> implements Specification<MatchableType> {

	private static final SuperclassSpecificationExtension[] EXTENSIONS;

	static {
		List<SuperclassSpecificationExtension> instances = TypeDiscovery.getUniqueInstancesOf(SuperclassSpecificationExtension.class);
		EXTENSIONS = instances.toArray(new SuperclassSpecificationExtension[instances.size()]);
	}

	@NonNull
	private final String name;

	@NonNull
	private final MatchableType rawElement;

	/**
	 * Creates a new {@link SuperclassSpecification} from the given super class.
	 *
	 * @param superClass
	 *            the super class
	 */
	public SuperclassSpecification(Class<?> superClass) {
		this(MatchableType.forClass(superClass));
	}

	/**
	 * Creates a new {@link SuperclassSpecification} from the given super class.
	 *
	 * @param superType
	 *            the super class
	 */
	public SuperclassSpecification(MatchableType superType) {
		rawElement = superType;
		this.name = rawElement == null ? ClassUtils.getName(Object.class) : ClassUtils.getName(rawElement);

		performExtensions(EXTENSIONS, this, rawElement);
	}
}
