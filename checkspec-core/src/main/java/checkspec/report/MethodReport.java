package checkspec.report;

import static checkspec.util.MessageUtils.bestFitting;
import static checkspec.util.MessageUtils.missing;
import static checkspec.util.MethodUtils.createString;

import java.lang.reflect.Method;

import checkspec.spec.MethodSpecification;
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
			return missing(createString(specMethod));
		} else if (getType() == ProblemType.SUCCESS) {
			return createString(specMethod);
		} else {
			return bestFitting(createString(getImplementation()), createString(specMethod));
		}
	}

	@Override
	protected String getRawTypeName(Method raw) {
		return raw.getName();
	}
}
