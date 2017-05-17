package checkspec.report;

import static checkspec.util.MessageUtils.bestFitting;
import static checkspec.util.MessageUtils.missing;
import static checkspec.util.MethodUtils.createString;

import java.lang.reflect.Method;

import checkspec.spec.MethodSpecification;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class MethodReport extends Report<MethodSpecification, Method> {

	public MethodReport(MethodSpecification specMethod) {
		super(specMethod, null, null);
	}

	public MethodReport(MethodSpecification specMethod, Method implementingMethod) {
		super(specMethod, implementingMethod, null);
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
