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
import java.util.function.BinaryOperator;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.report.ReportProblem;
import checkspec.report.ReportProblemType;
import checkspec.type.MatchableType;
import checkspec.util.MatchingState;
import lombok.experimental.UtilityClass;

/**
 * Miscellaneous methods for analyzing methods.
 *
 * @author Florian Cramer
 *
 */
@UtilityClass
class AnalysisUtils {

	/**
	 * Compares two {@link MatchableType}s and returns a problem, if one was
	 * found.
	 *
	 * @param specification
	 *            the specification
	 * @param actual
	 *            the possible implementation
	 * @param oldReports
	 *            the current matches
	 * @param compatible
	 *            the operator to apply for minor problem
	 * @param incompatible
	 *            the operator to apply for major problem
	 * @return a problem, if one was found
	 */
	public static Optional<ReportProblem> compareTypes(MatchableType specification, MatchableType actual, MultiValuedMap<Class<?>, Class<?>> oldReports, BinaryOperator<String> compatible,
			BinaryOperator<String> incompatible) {
		MatchingState state = specification.matches(actual, oldReports);
		if (state == MatchingState.FULL_MATCH) {
			return Optional.empty();
		}

		String specificationName = getName(specification);
		String actualName = getName(actual);
		if (state == MatchingState.PARTIAL_MATCH) {
			return Optional.of(new ReportProblem(5, compatible.apply(specificationName, actualName), ReportProblemType.WARNING));
		} else {
			return Optional.of(new ReportProblem(10, incompatible.apply(specificationName, actualName), ReportProblemType.ERROR));
		}
	}
}
