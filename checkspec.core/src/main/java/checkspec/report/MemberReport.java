package checkspec.report;

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

import checkspec.specification.Specification;
import lombok.EqualsAndHashCode;

/**
 * Abstract base class for all reports of members of classes such as field,
 * constructors and methods.
 *
 * @author Florian Cramer
 *
 * @param <MemberType>
 *            the member type
 * @param <SpecificationType>
 *            the specification type
 */
@EqualsAndHashCode(callSuper = true)
public abstract class MemberReport<MemberType extends Member, SpecificationType extends Specification<MemberType>> extends Report<MemberType, SpecificationType> {

	/**
	 * Creates a new empty {@link MemberReport} from the given specification.
	 *
	 * @param specification
	 *            the specification
	 */
	protected MemberReport(SpecificationType specification) {
		super(specification);
	}

	/**
	 * Creates a new {@link MemberReport} from the given specification and
	 * implementation.
	 *
	 * @param specification
	 *            the specification
	 * @param implementation
	 *            the implementation
	 */
	protected MemberReport(SpecificationType specification, MemberType implementation) {
		super(specification, implementation);
	}

}
