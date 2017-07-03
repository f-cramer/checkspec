package checkspec.report;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReportProblem {

	private static final String FORMAT = "%s [%d]";

	private final int score;
	@NonNull
	private final String content;
	@NonNull
	private final ReportProblemType type;

	@Override
	public String toString() {
		return score == 0 ? content : String.format(FORMAT, content, score);
	}
}
