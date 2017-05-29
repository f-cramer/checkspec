package checkspec.report;

import javax.annotation.Nonnull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReportProblem implements ReportEntry {

	private final int score;
	@Nonnull
	private final String content;
	@Nonnull
	private final Type type;

	@Override
	public String toString() {
		return score == 0 ? content : String.format("%s [%d]", content, score);
	}

	@RequiredArgsConstructor
	public static enum Type {
		WARNING(ProblemType.WARNING), ERROR(ProblemType.ERROR);

		@Nonnull
		private final ProblemType problemType;

		public ProblemType toProblemType() {
			return problemType;
		}
	}
}
