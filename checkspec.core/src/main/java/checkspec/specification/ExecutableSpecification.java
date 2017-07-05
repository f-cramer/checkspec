package checkspec.specification;

import java.lang.reflect.Executable;

public interface ExecutableSpecification<RawType extends Executable> extends MemberSpecification<RawType> {

	ParametersSpecification getParameters();
}
