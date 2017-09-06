package checkspec.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.report.ClassReport;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblemType;
import checkspec.specification.ClassSpecification;
import checkspec.specification.InterfaceSpecification;
import checkspec.type.MatchableType;
import checkspec.util.ClassUtils;

public class InterfaceAnalysis implements ClassAnalysis<List<ReportProblem>> {

	private static final String SHOULD = "should implement interface \"%s\"";
	private static final String SHOULD_NOT = "should not implement interface \"%s\"";

	@Override
	public List<ReportProblem> analyze(MatchableType actual, ClassSpecification spec, MultiValuedMap<Class<?>, Class<?>> oldReports) {
		List<ReportProblem> problems = new ArrayList<>();

		List<MatchableType> notFoundInterfaces = Arrays.stream(actual.getRawClass().getInterfaces())
				.map(MatchableType::forClass)
				// needs to be mutable
				.collect(Collectors.toCollection(() -> new ArrayList<>()));

		List<InterfaceSpecification> specifications = new ArrayList<>(Arrays.asList(spec.getInterfaceSpecifications()));

		for (InterfaceSpecification specification : specifications) {
			Optional<MatchableType> interf = notFoundInterfaces.parallelStream()
					.filter(i -> specification.getRawElement().matches(i, oldReports).evaluate(true, true, false))
					.findAny();

			if (interf.isPresent()) {
				notFoundInterfaces.remove(interf.get());
			} else {
				problems.add(new ReportProblem(15, String.format(SHOULD, ClassUtils.getName(specification.getRawElement())), ReportProblemType.ERROR));
			}
		}

		for (MatchableType notFoundInterface : notFoundInterfaces) {
			problems.add(new ReportProblem(15, String.format(SHOULD_NOT, ClassUtils.getName(notFoundInterface)), ReportProblemType.ERROR));
		}

		return problems;
	}

	@Override
	public void add(ClassReport report, List<ReportProblem> returnType) {
		report.addProblems(returnType);
	}
}
