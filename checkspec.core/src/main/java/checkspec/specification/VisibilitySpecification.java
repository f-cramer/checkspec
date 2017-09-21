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



import java.lang.annotation.Annotation;
import java.util.Arrays;

import checkspec.api.Spec;
import checkspec.api.Visibility;
import checkspec.util.MemberUtils;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

/**
 * A specification for the visibility of a type of class element.
 *
 * @author Florian Cramer
 *
 */
@Value
@EqualsAndHashCode
public class VisibilitySpecification implements Specification<Integer> {

	@NonNull
	private final Visibility[] visibilities;

	private final Integer rawElement;

	/**
	 * Creates a new {@link VisibilitySpecification} from the given modifiers
	 * and annotations.
	 *
	 * @param modifiers
	 *            the modifiers
	 * @param annotations
	 *            the annotations
	 */
	public VisibilitySpecification(int modifiers, Annotation[] annotations) {
		Visibility[] vis = Arrays.stream(annotations)
				.filter(Spec.class::isInstance)
				.map(Spec.class::cast)
				.findAny()
				.map(Spec::visibility)
				.orElseGet(() -> fromModifiers(modifiers));

		if (vis.length == 0) {
			vis = fromModifiers(modifiers);
		}

		visibilities = vis;
		rawElement = modifiers;
	}

	/**
	 * Returns whether or not the given visibility matches this specification.
	 *
	 * @param visibility
	 *            the visibility
	 * @return whether or not the given visibility matches this specification
	 */
	public boolean matches(Visibility visibility) {
		return Arrays.stream(visibilities).anyMatch(e -> e == Visibility.IRRELEVANT || e == visibility);
	}

	private Visibility[] fromModifiers(int modifiers) {
		return new Visibility[] { MemberUtils.getVisibility(modifiers) };
	}

	@Override
	public String getName() {
		return "";
	}
}
