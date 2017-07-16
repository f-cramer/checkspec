package checkspec.report.output.text;

import static org.fusesource.jansi.Ansi.*;

import java.io.IOException;
import java.io.Writer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import checkspec.report.Report;
import checkspec.report.ReportProblem;
import checkspec.report.ReportType;
import checkspec.report.SpecReport;
import checkspec.report.output.OutputException;
import checkspec.report.output.Outputter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TextOutputter implements Outputter {

	@NonNull
	private final Writer writer;
	private final boolean colored;

	@Override
	public void output(SpecReport report) throws OutputException {
		try {
			AnsiConsole.systemInstall();
			writer.write(toString(report));
			if (colored) {
				writer.write(ansi().reset().toString());
			}
			writer.write('\n');
			writer.flush();
		} catch (IOException e) {
			throw new OutputException(e);
		} finally {
			AnsiConsole.systemUninstall();
		}
	}

	private String toString(SpecReport report) {
		return report.getClassReports().parallelStream()
				.map(this::toString)
				.collect(Collectors.joining("\n", report.toString() + "\n", ""))
				.trim().replace("\n", "\n\t");
	}

	private String toString(final Report<?, ?> report) {
		Stream<String> problems = report.getProblems().parallelStream().map(this::toString);
		Stream<String> subReports = report.getSubReports().parallelStream().map(this::toString);

		String reportString = toString(report.toString(), report.getType());
		String rawString = Stream.concat(problems, subReports)
				.collect(Collectors.joining("\n", reportString + "\n", ""));
		while (rawString.endsWith("\n")) {
			rawString = rawString.substring(0, rawString.length() - 1);
		}
		return rawString.replace("\n", "\n\t");
	}

	private String toString(final ReportProblem problem) {
		return toString(problem.toString(), problem.getType().toReportType());
	}

	private String toString(final String string, final ReportType type) {
		if (colored) {
			Ansi ansi = ansi();
			switch (type) {
			case SUCCESS:
				ansi = ansi.fgBrightGreen();
				break;
			case WARNING:
				ansi = ansi.fgBrightYellow();
				break;
			case ERROR:
				ansi = ansi.fgBrightRed();
				break;
			}
			return ansi.a(string).toString();
		} else {
			return string;
		}
	}
}
