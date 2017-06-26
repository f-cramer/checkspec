package checkspec.analysis;

public interface Analysis<RawType, SpecificationType, ReturnType> {

	public ReturnType analyze(RawType actual, SpecificationType specification);
}
