package checkspec.analysis;

public interface Analysis<RawType, SpecificationType, ReturnType> {

	public ReturnType analyse(RawType actual, SpecificationType specification);
}
