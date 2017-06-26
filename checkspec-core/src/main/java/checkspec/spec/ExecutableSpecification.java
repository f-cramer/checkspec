package checkspec.spec;

import java.lang.reflect.Executable;

import checkspec.spec.ParametersSpecification;
import checkspec.spec.Specification;

public interface ExecutableSpecification<RawType extends Executable> extends Specification<RawType> {

	ParametersSpecification getParameters();
}
