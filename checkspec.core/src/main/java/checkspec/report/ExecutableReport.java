package checkspec.report;

import java.lang.reflect.Executable;
import java.util.Collections;
import java.util.List;

import checkspec.specification.ExecutableSpecification;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class ExecutableReport<RawType extends Executable, SpecificationType extends ExecutableSpecification<RawType>> extends MemberReport<RawType, SpecificationType> {

	private final ParametersReport parametersReport;

	public ExecutableReport(SpecificationType specification) {
		super(specification);
		this.parametersReport = new ParametersReport(specification.getParameters());
	}

	public ExecutableReport(SpecificationType specification, RawType executable, ParametersReport parametersReport) {
		super(specification, executable);
		this.parametersReport = parametersReport;
	}

	@Override
	public List<Report<?, ?>> getSubReports() {
		if (getImplementation() == null || parametersReport.getScore() == 0) {
			return Collections.emptyList();
		}
		return Collections.singletonList(parametersReport);
	}
}
