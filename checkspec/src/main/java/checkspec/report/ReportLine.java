package checkspec.report;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ReportLine implements ReportEntry {

	private final int score;
	private final String content;

	@Override
	public String toString() {
		return score == 0 ? content : String.format("%s [%d]", content, score);
	}
}
