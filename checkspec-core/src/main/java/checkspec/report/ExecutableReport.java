package checkspec.report;

import java.lang.reflect.Executable;
import java.util.Collections;
import java.util.List;

import checkspec.spec.ExecutableSpecification;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class ExecutableReport<SpecificationType extends ExecutableSpecification<RawType>, RawType extends Executable> extends Report<SpecificationType, RawType> {

	private final ParametersReport parametersReport;
	
	public ExecutableReport(SpecificationType specification) {
		super(specification, null, null);
		this.parametersReport = new ParametersReport(specification.getParameters());
	}
	
	public ExecutableReport(SpecificationType specification, RawType executable, ParametersReport parametersReport) {
		super(specification, executable, null);
		this.parametersReport = parametersReport;
	}

	@Override
	public List<Report<?, ?>> getSubReports() {
		if (getImplementation() == null) {
			return Collections.emptyList();
		}
		return Collections.singletonList(parametersReport);
	}
}
