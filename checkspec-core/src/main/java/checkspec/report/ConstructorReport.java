package checkspec.report;

import static checkspec.util.ConstructorUtils.createString;
import static checkspec.util.MessageUtils.bestFitting;
import static checkspec.util.MessageUtils.missing;

import java.lang.reflect.Constructor;

import checkspec.spec.ConstructorSpecification;

public class ConstructorReport extends ExecutableReport<Constructor<?>, ConstructorSpecification> {

	public ConstructorReport(ConstructorSpecification specification) {
		super(specification);
	}

	public ConstructorReport(ConstructorSpecification specification, Constructor<?> constructor, ParametersReport parametersReport) {
		super(specification, constructor, parametersReport);
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
		Constructor<?> specConstructor = getSpec().getRawElement();
		if (getImplementation() == null) {
			return missing(createString(specConstructor));
		} else if (getType() == ProblemType.SUCCESS) {
			return createString(specConstructor);
		} else {
			return bestFitting(createString(getImplementation()), createString(specConstructor));
		}
	}

	@Override
	protected String getRawTypeName(Constructor<?> raw) {
		return "";
	}
}
