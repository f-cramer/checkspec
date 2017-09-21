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

import java.lang.reflect.Executable;
import java.util.Collections;
import java.util.List;

import checkspec.specification.ExecutableSpecification;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class ExecutableReport<RawType extends Executable, SpecificationType extends ExecutableSpecification<RawType>> extends MemberReport<RawType, SpecificationType> {

	private final ParametersReport parametersReport;

	public ExecutableReport(SpecificationType specification) {
		super(specification);
		this.parametersReport = new ParametersReport(specification.getParameters());
	}

	public ExecutableReport(SpecificationType specification, RawType executable, ParametersReport parametersReport) {
		super(specification, executable);
		this.parametersReport = parametersReport;
	}

	@Override
	public List<Report<?, ?>> getSubReports() {
		if (getImplementation() == null || parametersReport.getScore() == 0) {
			return Collections.emptyList();
		}
		return Collections.singletonList(parametersReport);
	}
}
