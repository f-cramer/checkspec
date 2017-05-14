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

	private static String toString(Report<?, ?> report) {
		//@formatter:off
		return report.getProblems()
		             .parallelStream()
		             .map(ReportProblem::toString)
		             .collect(Collectors.joining("\n", report.toString() + "\n", ""))
		             .trim()
		             .replace("\n", "\n\t");
		//@formatter:on
	}

	private static String toString(ClassReport report) {
		Stream<String> problems = report.getProblems().parallelStream().map(Object::toString);

		//@formatter:off
		Stream<String> reports = Stream.of(report.getFieldReports(), report.getConstructorReports(), report.getMethodReports())
		                               .parallel()
		                               .flatMap(List::stream)
		                               .map(ConsoleOutputter::toString);
		//@formatter:on

		//@formatter:off
		return Stream.concat(problems, reports)
		             .collect(Collectors.joining("\n", report.toString() + "\n", ""))
		             .trim()
		             .replace("\n", "\n\t");
		//@formatter:on
	}

	private static String toString(SpecReport report) {
		//@formatter:off
		return report.getClassReports()
		             .parallelStream()
		             .map(ConsoleOutputter::toString)
		             .collect(Collectors.joining("\n", report.toString() + "\n", ""))
		             .trim()
		             .replace("\n", "\n\t");
		//@formatter:on
	}
}
