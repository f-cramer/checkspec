package checkspec.report;

import static checkspec.util.ConstructorUtils.createString;
import static checkspec.util.MessageUtils.bestFitting;
import static checkspec.util.MessageUtils.missing;

import java.lang.reflect.Constructor;

import checkspec.spec.ConstructorSpecification;

public class ConstructorReport extends Report<ConstructorSpecification, Constructor<?>> {

	public ConstructorReport(Constructor<?> specConstructor) {
		super(ConstructorSpecification.from(specConstructor), null, null);
	}

	public ConstructorReport(Constructor<?> specConstructor, Constructor<?> implementingConstructor) {
		super(ConstructorSpecification.from(specConstructor), implementingConstructor, null);
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
}
