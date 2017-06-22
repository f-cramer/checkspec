package checkspec.analysis;

import java.util.Optional;

import checkspec.report.ClassReport;
import checkspec.report.ReportProblem;

public interface OptionalProblemAdder {

	default void add(ClassReport report, Optional<ReportProblem> problem) {
		problem.ifPresent(report::addProblem);
	}
}
