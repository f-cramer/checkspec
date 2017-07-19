package checkspec.report;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import checkspec.specification.ParametersSpecification;
import checkspec.util.ClassUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ParametersReport extends Report<List<Parameter>, ParametersSpecification> {

	private static final String FINE = "parameters are fitting well";
	private static final String ERROR = "some parameter types are off";

	public ParametersReport(ParametersSpecification spec) {
		super(spec);
	}

	public ParametersReport(ParametersSpecification spec, Parameter[] implementation) {
		this(spec, Arrays.asList(implementation));
	}

	public ParametersReport(ParametersSpecification spec, List<Parameter> implementation) {
		super(spec, implementation);
	}

	@Override
	public String getTitle() {
		return getType() == ReportType.SUCCESS ? FINE : ERROR;
	}

	@Override
	protected String getRawTypeName(List<Parameter> raw) {
		return raw.parallelStream()
				.map(Parameter::getType)
				.map(ClassUtils::getName)
				.collect(Collectors.joining(", ", "(", ")"));
	}
}
