package checkspec.analysis;

import static checkspec.util.MemberUtils.getVisibility;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import checkspec.api.Visibility;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblem.Type;
import checkspec.specification.VisibilitySpecification;

public abstract class AbstractVisibilityAnalysis {

	private static final String SHOULD_NOT_HAVE_ANY = "should not have any visibility modifier";
	private static final String SHOULD_HAVE_SINGLE = "should haven visibility \"%s\"";
	private static final String SHOULD_HAVE_MULTIPLE = "should have any of the following visibilities: \"%s\"";

	protected static Optional<ReportProblem> analyseVisibility(int actualModifiers, VisibilitySpecification spec) {
		Visibility actualVisibility = getVisibility(actualModifiers);
		ReportProblem problem = null;

		if (!spec.matches(actualVisibility)) {
			Visibility[] visibilities = spec.getVisibilities();
			if (visibilities.length == 1 && visibilities[0] == Visibility.PACKAGE) {
				problem = new ReportProblem(1, SHOULD_NOT_HAVE_ANY, Type.ERROR);
			} else if (visibilities.length == 1) {
				problem = new ReportProblem(1, String.format(SHOULD_HAVE_SINGLE, visibilities[0]), Type.ERROR);
			} else {
				String visibilityString = Arrays.stream(visibilities)
						.map(Visibility::toString)
						.collect(Collectors.joining(", "));
				problem = new ReportProblem(1, String.format(SHOULD_HAVE_MULTIPLE, visibilityString), Type.ERROR);
			}
		}

		return Optional.ofNullable(problem);
	}
}
