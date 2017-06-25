package checkspec.report;

import static checkspec.util.MessageUtils.bestFitting;
import static checkspec.util.MessageUtils.missing;
import static checkspec.util.MethodUtils.createString;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import checkspec.spec.MethodSpecification;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class MethodReport extends Report<MethodSpecification, Method> {

	private final ParametersReport parametersReport;

	public MethodReport(MethodSpecification specification) {
		super(specification, null, null);
		this.parametersReport = new ParametersReport(specification.getParameters());
	}

	public MethodReport(MethodSpecification specification, Method method, ParametersReport parametersReport) {
		super(specification, method, null);
		this.parametersReport = parametersReport;
	}

	@Override
	public List<Report<?, ?>> getSubReports() {
		return Collections.singletonList(parametersReport);
	}

	@Override
	public String getTitle() {
		Method specMethod = getSpec().getRawElement();
		if (getImplementation() == null) {
			return missing(createString(specMethod));
		} else if (getType() == ProblemType.SUCCESS) {
			return createString(specMethod);
		} else {
			return bestFitting(createString(getImplementation()), createString(specMethod));
		}
	}
}
