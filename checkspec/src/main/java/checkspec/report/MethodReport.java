package checkspec.report;

import static checkspec.util.MessageUtils.bestFitting;
import static checkspec.util.MessageUtils.missing;
import static checkspec.util.MethodUtils.createString;

import java.lang.reflect.Method;

import checkspec.spec.MethodSpec;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class MethodReport extends Report<MethodSpec, Method> {

	public MethodReport(MethodSpec specMethod) {
		super(specMethod, null, null);
	}

	public MethodReport(MethodSpec specMethod, Method implementingMethod) {
		super(specMethod, implementingMethod, null);
	}

	@Override
	public int getScore() {
		if (getImplementation() == null) {
			return 10;
		} else {
			return super.getScore();
		}
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
