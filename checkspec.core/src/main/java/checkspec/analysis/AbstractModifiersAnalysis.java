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

import static javax.lang.model.element.Modifier.*;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import checkspec.report.ReportProblem;
import checkspec.report.ReportProblemType;
import checkspec.specification.ModifiersSpecification;
import checkspec.specification.ModifiersSpecification.State;

/**
 * An abstract analysis for modifiers of types and class elements.
 *
 * @author Florian Cramer
 *
 */
public abstract class AbstractModifiersAnalysis {

	private static final String SHOULD_HAVE = "should have modifier \"%s\"";
	private static final String SHOULD_NOT_HAVE = "should not have modifier \"%s\"";

	/**
	 * Analyzes the given set of modifiers with the specification.
	 *
	 * @param actual
	 *            the modifiers
	 * @param spec
	 *            the specification
	 * @param checkAbstract
	 *            whether or not to analyze {@code abstract} modifier
	 * @return a list of problems found while analyzing
	 */
	protected static List<ReportProblem> analyzeModifiers(int actual, ModifiersSpecification spec, boolean checkAbstract) {
		List<Optional<ReportProblem>> problems = new ArrayList<>();

		if (checkAbstract) {
			problems.add(analyze(Modifier.isAbstract(actual), spec.isAbstract(), ABSTRACT));
		}
		problems.add(analyze(Modifier.isFinal(actual), spec.isFinal(), FINAL));
		problems.add(analyze(Modifier.isNative(actual), spec.isNative(), NATIVE));
		problems.add(analyze(Modifier.isStatic(actual), spec.isStatic(), STATIC));
		problems.add(analyze(Modifier.isStrict(actual), spec.isStrict(), STRICTFP));
		problems.add(analyze(Modifier.isSynchronized(actual), spec.isSynchronized(), SYNCHRONIZED));
		problems.add(analyze(Modifier.isTransient(actual), spec.isTransient(), TRANSIENT));
		problems.add(analyze(Modifier.isVolatile(actual), spec.isVolatile(), VOLATILE));

		return problems.parallelStream()
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList());
	}

	/**
	 * Creates and returns a {@link ReportProblem} if the actual state of the
	 * given modifiers does not match the given modifier specification state.
	 *
	 * @param actual
	 *            the actual modifier state - {@code true} if the modifier is
	 *            set, {@code false} otherwise
	 * @param spec
	 *            the modifier specification
	 * @param modifier
	 *            the modifier itself
	 * @return an empty optional if the actual modifier matches the given
	 *         specification, an optional with value that contains a
	 *         {@link ReportProblem} with a matching problem description
	 */
	private static Optional<ReportProblem> analyze(boolean actual, State spec, javax.lang.model.element.Modifier modifier) {
		ReportProblem problem = null;

		if (spec == State.TRUE && !spec.matches(actual)) {
			problem = new ReportProblem(1, String.format(SHOULD_HAVE, modifier), ReportProblemType.WARNING);
		}

		if (spec == State.FALSE && !spec.matches(actual)) {
			problem = new ReportProblem(1, String.format(SHOULD_NOT_HAVE, modifier), ReportProblemType.WARNING);
		}

		return Optional.ofNullable(problem);
	}
}
