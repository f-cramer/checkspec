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



import java.lang.reflect.Member;

/**
 * Base interface for all specifications of class members such as field,
 * constructors and methods.
 *
 * @author Florian Cramer
 *
 * @param <MemberType>
 *            the member type
 */
public interface MemberSpecification<MemberType extends Member> extends Specification<MemberType> {

	/**
	 * Returns the modifiers sub specification for this specification.
	 *
	 * @return the modifiers specification
	 */
	public ModifiersSpecification getModifiers();

	/**
	 * Returns the visibility sub specification for this specification.
	 *
	 * @return the visibility specification
	 */
	public VisibilitySpecification getVisibility();
}
