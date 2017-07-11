package checkspec.analysis;

public interface AnalysisWithoutPayload<RawType, SpecificationType, ReturnType> extends Analysis<RawType, SpecificationType, ReturnType, Void> {

	default ReturnType analyze(RawType actual, SpecificationType specification) {
		return analyze(actual, specification, null);
	}
}
