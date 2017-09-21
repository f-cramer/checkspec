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

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import checkspec.extension.AbstractExtendable;
import checkspec.type.MatchableType;
import checkspec.util.FieldUtils;
import checkspec.util.TypeDiscovery;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

/**
 * A specification of a field.
 *
 * @author Florian Cramer
 *
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class FieldSpecification extends AbstractExtendable<FieldSpecification, Field> implements MemberSpecification<Field>, Comparable<FieldSpecification> {

	private static final FieldSpecificationExtension[] EXTENSIONS;

	static {
		List<FieldSpecificationExtension> instances = TypeDiscovery.getUniqueInstancesOf(FieldSpecificationExtension.class);
		EXTENSIONS = instances.toArray(new FieldSpecificationExtension[instances.size()]);
	}

	@NonNull
	private final String name;

	@NonNull
	private final MatchableType type;

	@NonNull
	private final ModifiersSpecification modifiers;

	@NonNull
	private final VisibilitySpecification visibility;

	@NonNull
	private final Field rawElement;

	/**
	 * Creates a new {@link FieldSpecification} from the given field.
	 *
	 * @param field
	 *            the field
	 */
	public FieldSpecification(Field field) {
		name = field.getName();
		type = FieldUtils.getType(field);
		modifiers = new ModifiersSpecification(field.getModifiers(), field.getAnnotations());
		visibility = new VisibilitySpecification(field.getModifiers(), field.getAnnotations());
		rawElement = field;

		performExtensions(EXTENSIONS, this, field);
	}

	@Override
	public int compareTo(FieldSpecification o) {
		return Objects.compare(name, o.name, Comparator.naturalOrder());
	}
}
