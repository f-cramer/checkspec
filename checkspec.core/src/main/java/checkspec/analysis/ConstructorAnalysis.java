package checkspec.analysis;

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

import java.lang.reflect.Constructor;
import java.util.Collection;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.report.ClassReport;
import checkspec.report.ConstructorReport;
import checkspec.report.ParametersReport;
import checkspec.specification.ClassSpecification;
import checkspec.specification.ConstructorSpecification;

/**
 * Analyzes the constructor(s) of a class.
 *
 * @author Florian Cramer
 *
 */
public class ConstructorAnalysis extends ExecutableAnalysis<Constructor<?>, ConstructorSpecification, ConstructorReport> {

	@Override
	protected ConstructorSpecification[] getMemberSpecifications(ClassSpecification spec) {
		return spec.getConstructors();
	}

	@Override
	protected Constructor<?>[] getMembers(Class<?> clazz) {
		return clazz.getDeclaredConstructors();
	}

	@Override
	protected ConstructorReport checkMember(Constructor<?> constructor, ConstructorSpecification spec, MultiValuedMap<Class<?>, Class<?>> oldReports) {
		ParametersReport parametersReport = PARAMETERS_ANALYSIS.analyze(constructor.getParameters(), spec.getParameters(), oldReports);
		ConstructorReport report = new ConstructorReport(spec, constructor, parametersReport);

		VISIBILITY_ANALYSIS.analyze(constructor, spec).ifPresent(report::addProblem);
		report.addProblems(MODIFIERS_ANALYSIS.analyze(constructor, spec));
		report.addProblems(EXCEPTION_ANALYSIS.analyze(constructor, spec, oldReports));

		return report;
	}

	@Override
	protected ConstructorReport createEmptyReport(ConstructorSpecification specification) {
		return new ConstructorReport(specification);
	}

	@Override
	public void add(ClassReport report, Collection<? extends ConstructorReport> returnType) {
		report.addConstructorReports(returnType);
	}
}
