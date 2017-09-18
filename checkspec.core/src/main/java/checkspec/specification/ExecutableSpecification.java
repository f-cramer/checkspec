package checkspec.specification;

import java.lang.reflect.Executable;

import checkspec.analysis.ExceptionSpecification;

public interface ExecutableSpecification<RawType extends Executable> extends MemberSpecification<RawType> {

	ParametersSpecification getParameters();

	ExceptionSpecification getExceptions();
}
