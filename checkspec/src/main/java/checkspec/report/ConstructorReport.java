package checkspec.report;

import static checkspec.util.ConstructorUtils.createString;
import static checkspec.util.MessageUtils.bestFitting;
import static checkspec.util.MessageUtils.missing;

import java.lang.reflect.Constructor;

public class ConstructorReport extends Report<Constructor<?>> {

	public ConstructorReport(Constructor<?> specConstructor) {
		super(specConstructor, null, null);
	}

	public ConstructorReport(Constructor<?> specConstructor, Constructor<?> implementingConstructor) {
		super(specConstructor, implementingConstructor, null);
	}

	public Constructor<?> getImplementingConstructor() {
		return super.getImplementingObject();
	}

	public Constructor<?> getSpecConstructor() {
		return super.getSpecObject();
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
		if (getImplementingObject() == null) {
			return 1;
		} else {
			return super.getScore();
		}
	}

	@Override
	public String getTitle() {
		Constructor<?> specConstructor = getSpecObject();
		if (isSuccess()) {
			return createString(specConstructor);
		} else {
			return toString(specConstructor, getImplementingObject());
		}
	}
}
