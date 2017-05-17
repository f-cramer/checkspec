package checkspec.report;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReportProblem implements ReportEntry {

	private final int score;
	@NonNull
	private final String content;
	@NonNull
	private final Type type;

	@Override
	public String toString() {
		return score == 0 ? content : String.format("%s [%d]", content, score);
	}

	@RequiredArgsConstructor
	public static enum Type {
		WARNING(ProblemType.WARNING), ERROR(ProblemType.ERROR);

		@NonNull
		private final ProblemType problemType;

		public ProblemType toProblemType() {
			return problemType;
		}
	}
}
