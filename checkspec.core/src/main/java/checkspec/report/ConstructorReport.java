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

import static checkspec.util.ConstructorUtils.*;
import static checkspec.util.MessageUtils.*;

import java.lang.reflect.Constructor;

import checkspec.specification.ConstructorSpecification;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A report for a constructor.
 *
 * @author Florian Cramer
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class ConstructorReport extends ExecutableReport<Constructor<?>, ConstructorSpecification> {

	/**
	 * Creates a new empty {@link ConstructorReport} from the given
	 * specification.
	 *
	 * @param specification
	 *            the specification
	 */
	public ConstructorReport(ConstructorSpecification specification) {
		super(specification);
	}

	/**
	 * Creates a new {@link ConstructorReport} from the given specification,
	 * implementation and parameters report.
	 *
	 * @param specification
	 *            the specification
	 * @param constructor
	 *            the implementation
	 * @param parametersReport
	 *            the parameters report
	 */
	public ConstructorReport(ConstructorSpecification specification, Constructor<?> constructor, ParametersReport parametersReport) {
		super(specification, constructor, parametersReport);
	}

	@Override
	public String getTitle() {
		Constructor<?> specConstructor = getSpecification().getRawElement();
		if (getImplementation() == null) {
			return missing(createString(specConstructor));
		} else if (getType() == ReportType.SUCCESS) {
			return createString(specConstructor);
		} else {
			return bestFitting(createString(getImplementation()), createString(specConstructor));
		}
	}

	@Override
	protected String getRawTypeName(Constructor<?> raw) {
		return "";
	}
}
