package checkspec.report;

import java.lang.reflect.Parameter;

import checkspec.spec.ParametersSpecification;

public class ParametersReport extends Report<ParametersSpecification, Parameter[]> {

	public ParametersReport(ParametersSpecification spec) {
		super(spec, null, null);
	}

	public ParametersReport(ParametersSpecification spec, Parameter[] implementation) {
		super(spec, implementation, null);
	}
}
