package checkspec.report;

import static checkspec.util.MessageUtils.*;

import java.lang.reflect.Method;

import checkspec.specification.MethodSpecification;
import checkspec.util.MethodUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class MethodReport extends ExecutableReport<Method, MethodSpecification> {

	public MethodReport(MethodSpecification specification) {
		super(specification);
	}

	public MethodReport(MethodSpecification specification, Method method, ParametersReport parametersReport) {
		super(specification, method, parametersReport);
	}

	@Override
	public String getTitle() {
		Method specMethod = getSpec().getRawElement();
		if (getImplementation() == null) {
			return missing(MethodUtils.toString(specMethod));
		} else if (getType() == ReportType.SUCCESS) {
			return MethodUtils.toString(specMethod);
		} else {
			return bestFitting(MethodUtils.toString(getImplementation()), MethodUtils.toString(specMethod));
		}
	}

	@Override
	protected String getRawTypeName(Method raw) {
		return raw.getName();
	}
}