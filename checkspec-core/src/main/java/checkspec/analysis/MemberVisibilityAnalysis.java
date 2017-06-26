package checkspec.analysis;

import java.lang.reflect.Member;
import java.util.Optional;

import checkspec.report.ReportProblem;
import checkspec.spec.Specification;

public class MemberVisibilityAnalysis extends AbstractVisibilityAnalysis implements Analysis<Member, Specification<? extends Member>, Optional<ReportProblem>> {

	@Override
	public Optional<ReportProblem> analyze(Member actual, Specification<? extends Member> specification) {
		return analyseVisibility(actual.getModifiers(), specification.getVisibility());
	}

}
