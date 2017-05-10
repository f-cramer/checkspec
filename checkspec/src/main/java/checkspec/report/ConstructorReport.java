package checkspec.report;

import static checkspec.util.ConstructorUtils.createString;
import static checkspec.util.MessageUtils.bestFitting;
import static checkspec.util.MessageUtils.missing;

import java.lang.reflect.Constructor;

import checkspec.spec.ConstructorSpec;

public class ConstructorReport extends Report<ConstructorSpec, Constructor<?>> {

	public ConstructorReport(Constructor<?> specConstructor) {
		super(ConstructorSpec.from(specConstructor), null, null);
	}

	public ConstructorReport(Constructor<?> specConstructor, Constructor<?> implementingConstructor) {
		super(ConstructorSpec.from(specConstructor), implementingConstructor, null);
	}

	private static String toString(Constructor<?> specConstructor, Constructor<?> implementingConstructor) {
		if (implementingConstructor == null) {
			return missing(createString(specConstructor));
		} else {
			return bestFitting(createString(implementingConstructor), createString(specConstructor));
		}
	}

	@Override
	public int getScore() {
		if (getImplementation() == null) {
			return 1;
		} else {
			return super.getScore();
		}
	}

	@Override
	public String getTitle() {
		Constructor<?> specConstructor = getSpec().getRawElement();
		if (isSuccess()) {
			return createString(specConstructor);
		} else {
			return toString(specConstructor, getImplementation());
		}
	}
}
