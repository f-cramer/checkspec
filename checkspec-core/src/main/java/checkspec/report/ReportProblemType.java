package checkspec.report;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ReportProblemType {
	WARNING(ProblemType.WARNING), ERROR(ProblemType.ERROR);

	@NonNull
	private final ProblemType problemType;

	public ProblemType toProblemType() {
		return problemType;
	}
}