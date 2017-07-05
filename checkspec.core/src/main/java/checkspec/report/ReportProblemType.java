package checkspec.report;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ReportProblemType {
	WARNING(ReportType.WARNING), ERROR(ReportType.ERROR);

	@NonNull
	private final ReportType problemType;

	public ReportType toProblemType() {
		return problemType;
	}
}