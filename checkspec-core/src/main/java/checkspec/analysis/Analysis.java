package checkspec.analysis;

import checkspec.spec.Specification;

public interface Analysis<RawType, SpecificationType extends Specification<? extends RawType>, ReturnType> {

	public ReturnType analyse(RawType actual, SpecificationType specification);
}
