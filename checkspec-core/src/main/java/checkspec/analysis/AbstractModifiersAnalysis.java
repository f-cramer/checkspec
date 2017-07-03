package checkspec.analysis;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.NATIVE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.element.Modifier.STRICTFP;
import static javax.lang.model.element.Modifier.SYNCHRONIZED;
import static javax.lang.model.element.Modifier.TRANSIENT;
import static javax.lang.model.element.Modifier.VOLATILE;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import checkspec.report.ReportProblem;
import checkspec.report.ReportProblemType;
import checkspec.specification.ModifiersSpecification;
import checkspec.specification.ModifiersSpecification.State;

public abstract class AbstractModifiersAnalysis {

	private static final String SHOULD_HAVE = "should have modifier \"%s\"";
	private static final String SHOULD_NOT_HAVE = "should not have modifier \"%s\"";

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
	 * Creates and returns a {@link ReportProblem} if the actual state of the given
	 * modifiers does not match the given modifier specification state.
	 *
	 * @param actual
	 *            the actual modifier state - {@code true} if the modifier is set,
	 *            {@code false} otherwise
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
