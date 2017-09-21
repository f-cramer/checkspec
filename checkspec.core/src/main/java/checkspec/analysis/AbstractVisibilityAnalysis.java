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



import static checkspec.util.MemberUtils.*;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import checkspec.api.Visibility;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblemType;
import checkspec.specification.VisibilitySpecification;

/**
 * An abstract analysis for the visibility of types and class elements.
 *
 * @author Florian Cramer
 *
 */
public abstract class AbstractVisibilityAnalysis {

	private static final String SHOULD_NOT_HAVE_ANY = "should not have any visibility modifier";
	private static final String SHOULD_HAVE_SINGLE = "should have visibility \"%s\"";
	private static final String SHOULD_HAVE_MULTIPLE = "should have any of the following visibilities: \"%s\"";

	/**
	 * Analyzes the given visibility with the given specification.
	 *
	 * @param actualModifiers
	 *            the visibility
	 * @param spec
	 *            the specification
	 * @return a problem if one was found
	 */
	protected static Optional<ReportProblem> analyseVisibility(int actualModifiers, VisibilitySpecification spec) {
		Visibility actualVisibility = getVisibility(actualModifiers);
		ReportProblem problem = null;

		if (!spec.matches(actualVisibility)) {
			Visibility[] visibilities = spec.getVisibilities();
			if (visibilities.length == 1 && visibilities[0] == Visibility.PACKAGE) {
				problem = new ReportProblem(1, SHOULD_NOT_HAVE_ANY, ReportProblemType.ERROR);
			} else if (visibilities.length == 1) {
				problem = new ReportProblem(1, String.format(SHOULD_HAVE_SINGLE, visibilities[0]), ReportProblemType.ERROR);
			} else {
				String visibilityString = Arrays.stream(visibilities)
						.map(Visibility::toString)
						.collect(Collectors.joining(", "));
				problem = new ReportProblem(1, String.format(SHOULD_HAVE_MULTIPLE, visibilityString), ReportProblemType.ERROR);
			}
		}

		return Optional.ofNullable(problem);
	}
}
