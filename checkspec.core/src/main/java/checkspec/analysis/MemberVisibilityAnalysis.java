package checkspec.analysis;

import java.lang.reflect.Member;
import java.util.Optional;

import checkspec.report.ReportProblem;
import checkspec.specification.MemberSpecification;

public class MemberVisibilityAnalysis extends AbstractVisibilityAnalysis implements Analysis<Member, MemberSpecification<? extends Member>, Optional<ReportProblem>> {

	@Override
	public Optional<ReportProblem> analyze(Member actual, MemberSpecification<? extends Member> specification) {
		return analyseVisibility(actual.getModifiers(), specification.getVisibility());
	}
}
