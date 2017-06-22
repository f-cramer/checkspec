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
import checkspec.report.ReportProblem.Type;
import checkspec.spec.ModifiersSpecification;
import checkspec.spec.ModifiersSpecification.State;

public abstract class AbstractModifiersAnalysis {

	protected static List<ReportProblem> analyse(int actual, ModifiersSpecification spec, boolean checkAbstract) {
		List<Optional<ReportProblem>> problems = new ArrayList<>();

		if (checkAbstract) {
			problems.add(analyse(Modifier.isAbstract(actual), spec.isAbstract(), ABSTRACT));
		}
		problems.add(analyse(Modifier.isFinal(actual), spec.isFinal(), FINAL));
		problems.add(analyse(Modifier.isNative(actual), spec.isNative(), NATIVE));
		problems.add(analyse(Modifier.isStatic(actual), spec.isStatic(), STATIC));
		problems.add(analyse(Modifier.isStrict(actual), spec.isStrict(), STRICTFP));
		problems.add(analyse(Modifier.isSynchronized(actual), spec.isSynchronized(), SYNCHRONIZED));
		problems.add(analyse(Modifier.isTransient(actual), spec.isTransient(), TRANSIENT));
		problems.add(analyse(Modifier.isVolatile(actual), spec.isVolatile(), VOLATILE));

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
	private static Optional<ReportProblem> analyse(boolean actual, State spec, javax.lang.model.element.Modifier modifier) {
		ReportProblem problem = null;

		if (spec == State.TRUE && !spec.matches(actual)) {
			String format = "should have modifier \"%s\"";
			problem = new ReportProblem(1, String.format(format, modifier), Type.WARNING);
		}

		if (spec == State.FALSE && !spec.matches(actual)) {
			String format = "should not have modifier \"%s\"";
			problem = new ReportProblem(1, String.format(format, modifier), Type.WARNING);
		}

		return Optional.ofNullable(problem);
	}
}
