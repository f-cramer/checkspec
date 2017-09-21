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

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.stream.Collectors;

import checkspec.specification.ParametersSpecification;
import checkspec.util.ClassUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A report for parameters of a constructor or a method.
 *
 * @author Florian Cramer
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class ParametersReport extends Report<List<Parameter>, ParametersSpecification> {

	private static final String FINE = "parameters are fitting well";
	private static final String ERROR = "some parameter types are off";

	/**
	 * Creates a new empty {@link ParametersReport} from the given
	 * specification.
	 *
	 * @param specification
	 *            the specification
	 */
	public ParametersReport(ParametersSpecification specification) {
		super(specification);
	}

	/**
	 * Creates a new {@link ParametersReport} from the given specification and
	 * implementations.
	 *
	 * @param specification
	 *            the specification
	 * @param implementations
	 *            the implementations
	 */
	public ParametersReport(ParametersSpecification specification, List<Parameter> implementations) {
		super(specification, implementations);
	}

	@Override
	public String getTitle() {
		return getType() == ReportType.SUCCESS ? FINE : ERROR;
	}

	@Override
	protected String getRawTypeName(List<Parameter> raw) {
		return raw.parallelStream()
				.map(Parameter::getType)
				.map(ClassUtils::getName)
				.collect(Collectors.joining(", ", "(", ")"));
	}
}
