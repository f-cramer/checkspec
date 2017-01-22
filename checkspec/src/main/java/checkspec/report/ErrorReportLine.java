package checkspec.report;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ErrorReportLine implements ErrorReportEntry {

	private final int score;
	private final String content;

	@Override
	public String toString() {
		return String.format("%s [%d]", content, score);
	}
}
