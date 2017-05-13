package checkspec.report.output;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import checkspec.report.ClassReport;
import checkspec.report.Report;
import checkspec.report.ReportProblem;
import checkspec.report.SpecReport;

public class ConsoleOutputter implements Outputter {

	@Override
	public void output(SpecReport report) {
		System.out.println(toString(report));
	}

	private String toString(Report<?, ?> report) {
		//@formatter:off
		return report.getProblems()
		             .parallelStream()
		             .map(ReportProblem::toString)
		             .collect(Collectors.joining("\n", report.toString() + "\n", ""))
		             .trim()
		             .replace("\n", "\n\t");
		//@formatter:on
	}

	private String toString(ClassReport report) {
		//@formatter:off
		Stream<String> problems = report.getProblems().parallelStream().map(Object::toString);

		Stream<String> reports = Stream.of(report.getFieldReports(), report.getConstructorReports(), report.getMethodReports())
		                                     .parallel()
		                                     .flatMap(List::stream)
		                                     .map(this::toString);

		return Stream.concat(problems, reports)
		             .collect(Collectors.joining("\n", report.toString() + "\n", ""))
		             .trim()
		             .replace("\n", "\n\t");
		//@formatter:on
	}

	private String toString(SpecReport report) {
		//@formatter:off
		return report.getClassReports()
		             .parallelStream()
		             .map(this::toString)
		             .collect(Collectors.joining("\n", report.toString() + "\n", ""))
		             .trim()
		             .replace("\n", "\n\t");
		//@formatter:on
	}
}
