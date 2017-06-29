package checkspec.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import checkspec.report.ClassReport;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblem.Type;
import checkspec.specification.ClassSpecification;
import checkspec.specification.InterfaceSpecification;
import checkspec.spring.ResolvableType;
import checkspec.util.ClassUtils;

public class InterfaceAnalysis implements AnalysisForClass<List<ReportProblem>> {

	private static final String SHOULD = "should implement interface \"%s\"";
	private static final String SHOULD_NOT = "should not implement interface \"%s\"";

	@Override
	public List<ReportProblem> analyze(ResolvableType actual, ClassSpecification spec) {
		List<ReportProblem> problems = new ArrayList<>();

		List<ResolvableType> notFoundInterfaces = Arrays.stream(actual.getRawClass().getInterfaces())
				.map(ResolvableType::forClass)
				// needs to be mutable
				.collect(Collectors.toCollection(() -> new ArrayList<>()));

		List<InterfaceSpecification> specifications = new ArrayList<>(Arrays.asList(spec.getInterfaceSpecifications()));

		for (InterfaceSpecification specification : specifications) {
			Optional<ResolvableType> interf = notFoundInterfaces.parallelStream()
					.filter(i -> ClassUtils.equal(specification.getRawElement(), i))
					.findAny();

			if (interf.isPresent()) {
				notFoundInterfaces.remove(interf.get());
			} else {
				problems.add(new ReportProblem(1, String.format(SHOULD, ClassUtils.getName(interf.get())), Type.ERROR));
			}
		}

		for (ResolvableType notFoundInterface : notFoundInterfaces) {
			problems.add(new ReportProblem(1, String.format(SHOULD_NOT, ClassUtils.getName(notFoundInterface)), Type.ERROR));
		}

		return problems;
	}

	@Override
	public void add(ClassReport report, List<ReportProblem> returnType) {
		report.addProblems(returnType);
	}
}
