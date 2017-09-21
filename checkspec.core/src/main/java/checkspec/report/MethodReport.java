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

import java.lang.reflect.Method;

import checkspec.specification.MethodSpecification;
import checkspec.util.MethodUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A report for a method.
 *
 * @author Florian Cramer
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class MethodReport extends ExecutableReport<Method, MethodSpecification> {

	/**
	 * Creates a new empty {@link MethodReport} from the given specification.
	 *
	 * @param specification
	 *            the specification
	 */
	public MethodReport(MethodSpecification specification) {
		super(specification);
	}

	/**
	 * Creates a new {@link MethodReport} from the given specification,
	 * implementation and parameters report.
	 *
	 * @param specification
	 *            the specification
	 * @param method
	 *            the implementation
	 * @param parametersReport
	 *            the parameters report for the specification and implementation
	 */
	public MethodReport(MethodSpecification specification, Method method, ParametersReport parametersReport) {
		super(specification, method, parametersReport);
	}

	@Override
	public String getTitle() {
		Method specMethod = getSpecification().getRawElement();
		if (getImplementation() == null) {
			return missing(MethodUtils.createString(specMethod));
		} else if (getType() == ReportType.SUCCESS) {
			return MethodUtils.createString(specMethod);
		} else {
			return bestFitting(MethodUtils.createString(getImplementation()), MethodUtils.createString(specMethod));
		}
	}

	@Override
	protected String getRawTypeName(Method raw) {
		return raw.getName();
	}
}
