package checkspec.analysis;

public interface Analysis<RawType, SpecificationType, ReturnType, Payload> {

	public ReturnType analyze(RawType actual, SpecificationType specification, Payload payload);
}
