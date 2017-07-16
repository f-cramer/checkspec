package checkspec.report;

import lombok.NonNull;
import lombok.Value;

@Value
public class ReportProblem {

	private static final String FORMAT = "%s [%d]";

	private final int score;
	@NonNull
	private final String message;
	@NonNull
	private final ReportProblemType type;

	@Override
	public String toString() {
		return score == 0 ? message : String.format(FORMAT, message, score);
	}
}
