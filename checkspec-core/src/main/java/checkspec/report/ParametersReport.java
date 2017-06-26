package checkspec.report;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.stream.Collectors;

import checkspec.spec.ParametersSpecification;
import checkspec.util.ClassUtils;

public class ParametersReport extends Report<ParametersSpecification, Parameter[]> {

	private static final String FINE = "parameters are fitting well";
	private static final String ERROR = "some parameter types are off";
	
	public ParametersReport(ParametersSpecification spec) {
		super(spec, null, null);
	}

	public ParametersReport(ParametersSpecification spec, Parameter[] implementation) {
		super(spec, implementation, null);
	}

	@Override
	public String getTitle() {
		return getType() == ProblemType.SUCCESS ? FINE : ERROR;
	}

	@Override
	protected String getRawTypeName(Parameter[] raw) {
		return Arrays.stream(raw).parallel()
				.map(Parameter::getType)
				.map(ClassUtils::getName)
				.collect(Collectors.joining(", ", "(", ")"));
	}
}
