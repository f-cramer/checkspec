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

import java.util.Optional;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.report.ClassReport;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblemType;
import checkspec.specification.ClassSpecification;
import checkspec.type.MatchableType;

/**
 * Analyzes the name of a class.
 *
 * @author Florian Cramer
 *
 */
public class ClassNameAnalysis implements ClassAnalysis<Optional<ReportProblem>> {

	private static final String SHOULD = "should be called \"%s\"";

	@Override
	public Optional<ReportProblem> analyze(MatchableType actual, ClassSpecification specification, MultiValuedMap<Class<?>, Class<?>> payload) {
		String specificationName = specification.getName();
		String implementationName = actual.getRawClass().getName();

		if (specificationName.equals(implementationName)) {
			return Optional.empty();
		} else {
			return Optional.of(new ReportProblem(25, String.format(SHOULD, specificationName), ReportProblemType.ERROR));
		}
	}

	@Override
	public void add(ClassReport report, Optional<ReportProblem> returnType) {
		returnType.ifPresent(report::addProblem);
	}
}
