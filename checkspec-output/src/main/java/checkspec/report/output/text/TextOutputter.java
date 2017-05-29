package checkspec.report.output.text;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import checkspec.report.ClassReport;
import checkspec.report.Report;
import checkspec.report.ReportProblem;
import checkspec.report.SpecReport;
import checkspec.report.output.OutputException;
import checkspec.report.output.Outputter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TextOutputter implements Outputter {

	@Nonnull
	private final Writer writer;

	@Override
	public void output(SpecReport report) throws OutputException {
		try {
			writer.write(toString(report));
			writer.write('\n');
			writer.flush();
		} catch (IOException e) {
			throw new OutputException(e);
		}
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
		                               .map(TextOutputter::toString);
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
		             .map(TextOutputter::toString)
		             .collect(Collectors.joining("\n", report.toString() + "\n", ""))
		             .trim()
		             .replace("\n", "\n\t");
		//@formatter:on
	}
}
