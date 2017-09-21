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



import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import checkspec.extension.AbstractExtendable;
import checkspec.type.MatchableType;
import checkspec.util.ClassUtils;
import checkspec.util.TypeDiscovery;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

/**
 * A specification for an interface that is implemented by a class.
 *
 * @author Florian Cramer
 *
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class InterfaceSpecification extends AbstractExtendable<InterfaceSpecification, MatchableType> implements Specification<MatchableType>, Comparable<InterfaceSpecification> {

	private static final InterfaceSpecificationExtension[] EXTENSIONS;

	static {
		List<InterfaceSpecificationExtension> instances = TypeDiscovery.getUniqueInstancesOf(InterfaceSpecificationExtension.class);
		EXTENSIONS = instances.toArray(new InterfaceSpecificationExtension[instances.size()]);
	}

	@NonNull
	private final String name;

	@NonNull
	private final MatchableType rawElement;

	/**
	 * Creates a new {@link InterfaceSpecification} from the given interface
	 * type.
	 *
	 * @param interf
	 *            the interface type
	 */
	public InterfaceSpecification(MatchableType interf) {
		this.name = ClassUtils.getName(interf);
		rawElement = interf;

		performExtensions(EXTENSIONS, this, rawElement);
	}

	@Override
	public int compareTo(InterfaceSpecification o) {
		return Objects.compare(name, o.name, Comparator.naturalOrder());
	}
}
