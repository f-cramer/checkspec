package checkspec.report.output.text;

/*-
 * #%L
 * CheckSpec Output
 * %%
 * Copyright (C) 2017 Florian Cramer
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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

/**
 * Represent an {@link Outputter} that outputs a {@link SpecReport} to a given
 * writer.
 *
 * @author Florian Cramer
 *
 */
@RequiredArgsConstructor
public class TextOutputter implements Outputter {

	@NonNull
	private final Writer writer;
	private final boolean colored;
	private final boolean bright;

	public TextOutputter(Writer writer, boolean colored) {
		this(writer, colored, true);
	}

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
				ansi = getGreen(ansi);
				break;
			case WARNING:
				ansi = getYellow(ansi);
				break;
			case ERROR:
				ansi = getRed(ansi);
				break;
			}
			return ansi.a(string).toString();
		} else {
			return string;
		}
	}

	private Ansi getGreen(Ansi ansi) {
		return bright ? ansi.fgBrightGreen() : ansi.fgGreen();
	}

	private Ansi getYellow(Ansi ansi) {
		return bright ? ansi.fgBrightYellow() : ansi.fgYellow();
	}

	private Ansi getRed(Ansi ansi) {
		return bright ? ansi.fgBrightRed() : ansi.fgRed();
	}
}
