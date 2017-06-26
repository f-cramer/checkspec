package checkspec.analysis;

import java.lang.reflect.Member;
import java.util.List;

import checkspec.report.ReportProblem;
import checkspec.spec.Specification;

public class MemberModifiersAnalysis extends AbstractModifiersAnalysis implements Analysis<Member, Specification<? extends Member>, List<ReportProblem>> {

	@Override
	public List<ReportProblem> analyze(Member actual, Specification<? extends Member> specification) {
		boolean checkAbstract = !specification.getRawElement().getDeclaringClass().isInterface() || actual.getDeclaringClass().isInterface();
		return analyze(actual.getModifiers(), specification.getModifiers(), checkAbstract);
	}

}
