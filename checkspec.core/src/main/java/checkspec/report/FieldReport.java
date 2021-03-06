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

import static checkspec.util.MessageUtils.*;

import java.lang.reflect.Field;

import checkspec.specification.FieldSpecification;
import checkspec.util.FieldUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A report for a field.
 *
 * @author Florian Cramer
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class FieldReport extends Report<Field, FieldSpecification> {

	/**
	 * Creates a new empty {@link FieldReport} from the given specification.
	 *
	 * @param specification
	 *            the specification
	 */
	public FieldReport(FieldSpecification specification) {
		super(specification);
	}

	/**
	 * Creates a new {@link FieldReport} from the given specification and
	 * implementation.
	 *
	 * @param specification
	 *            the specification
	 * @param implementation
	 *            the implementation
	 */
	public FieldReport(FieldSpecification specification, Field implementation) {
		super(specification, implementation);
	}

	@Override
	public String getTitle() {
		Field specField = getSpecification().getRawElement();
		if (getImplementation() == null) {
			return missing(FieldUtils.createString(specField));
		} else if (getType() == ReportType.SUCCESS) {
			return FieldUtils.createString(specField);
		} else {
			return bestFitting(FieldUtils.createString(getImplementation()), FieldUtils.createString(specField));
		}
	}

	@Override
	protected String getRawTypeName(Field raw) {
		return raw.getName();
	}
}
