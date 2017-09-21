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



import static checkspec.util.ClassUtils.*;

import java.util.Optional;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.report.ClassReport;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblemType;
import checkspec.specification.ClassSpecification;
import checkspec.type.MatchableType;
import checkspec.util.MatchingState;

/**
 * Analyzes the super class of a type.
 *
 * @author Florian Cramer
 *
 */
public class SuperclassAnalysis implements ClassAnalysis<Optional<ReportProblem>> {

	private static final MatchableType OBJECT = MatchableType.OBJECT;
	private static final String HAS_BUT_SHOULD_NOT = "should not declare any super class";
	private static final String DECLARES_COMPATIBLE = "declares compatible super class \"%s\"";
	private static final String INCORRECT_GENERICS = "super type has incorrect generics";
	private static final String SHOULD_DECLARE_DIFFERENT = "should declare \"%s\" as its super class";

	@Override
	public Optional<ReportProblem> analyze(MatchableType actual, ClassSpecification specification, MultiValuedMap<Class<?>, Class<?>> oldReports) {
		MatchableType specificationSuperType = specification.getSuperclassSpecification().getRawElement();
		MatchableType actualSuperType = actual.getSuperType();

		MatchingState state = specificationSuperType.matches(actualSuperType, oldReports);
		if (state == MatchingState.FULL_MATCH) {
			return Optional.empty();
		}

		if (OBJECT == specificationSuperType && OBJECT != actualSuperType) {
			return Optional.of(new ReportProblem(20, HAS_BUT_SHOULD_NOT, ReportProblemType.WARNING));
		}

		if (state == MatchingState.PARTIAL_MATCH) {
			String message = String.format(DECLARES_COMPATIBLE, getName(specificationSuperType));
			return Optional.of(new ReportProblem(20, message, ReportProblemType.ERROR));
		}

		if (specificationSuperType.getRawClass() == actualSuperType.getRawClass()) {
			return Optional.of(new ReportProblem(10, INCORRECT_GENERICS, ReportProblemType.WARNING));
		}

		String message = String.format(SHOULD_DECLARE_DIFFERENT, getName(specificationSuperType));
		return Optional.of(new ReportProblem(20, message, ReportProblemType.ERROR));
	}

	@Override
	public void add(ClassReport report, Optional<ReportProblem> returnType) {
		returnType.ifPresent(report::addProblem);
	}
}
