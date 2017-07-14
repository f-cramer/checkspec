package checkspec.report.output.text;

import java.io.IOException;
import java.io.Writer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import checkspec.report.Report;
import checkspec.report.ReportProblem;
import checkspec.report.SpecReport;
import checkspec.report.output.OutputException;
import checkspec.report.output.Outputter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TextOutputter implements Outputter {

	@NonNull
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
		Stream<String> problems = report.getProblems().parallelStream()
				.map(ReportProblem::toString);
		Stream<String> subReports = report.getSubReports().parallelStream()
				.map(TextOutputter::toString);
		return Stream.concat(problems, subReports)
				.collect(Collectors.joining("\n", report.toString() + "\n", ""))
				.trim()
				.replace("\n", "\n\t");
	}

	private static String toString(SpecReport report) {
		return report.getClassReports().parallelStream()
				.map(TextOutputter::toString)
				.collect(Collectors.joining("\n", report.toString() + "\n", ""))
				.trim()
				.replace("\n", "\n\t");
	}
}
