package checkspec.analysis;

import static checkspec.util.MemberUtils.getVisibility;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import checkspec.api.Visibility;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblem.Type;
import checkspec.spec.VisibilitySpecification;

public abstract class AbstractVisibilityAnalysis {

	protected static Optional<ReportProblem> analyseVisibility(int actualModifiers, VisibilitySpecification spec) {
		Visibility actualVisibility = getVisibility(actualModifiers);
		ReportProblem problem = null;

		if (!spec.matches(actualVisibility)) {
			Visibility[] visibilities = spec.getVisibilities();
			if (visibilities.length == 1 && visibilities[0] == Visibility.PACKAGE) {
				problem = new ReportProblem(1, "should not have any visibility modifier", Type.ERROR);
			} else if (visibilities.length == 1) {
				problem = new ReportProblem(1, String.format("should have visibility \"%s\"", visibilities[0]), Type.ERROR);
			} else {
				String visibilityString = Arrays.stream(visibilities)
						.map(Visibility::toString)
						.collect(Collectors.joining(", "));
				problem = new ReportProblem(1, String.format("should have any of the following visibilities: \"%s\"", visibilityString), Type.ERROR);
			}
		}

		return Optional.ofNullable(problem);
	}
}
