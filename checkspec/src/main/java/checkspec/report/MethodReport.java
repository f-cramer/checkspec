package checkspec.report;

import static checkspec.util.MethodUtils.createString;
import static checkspec.util.MessageUtils.bestFitting;
import static checkspec.util.MessageUtils.missing;

import java.lang.reflect.Method;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class MethodReport extends Report<Method> {

	public MethodReport(Method specMethod) {
		super(specMethod, null, null);
	}

	public MethodReport(Method specMethod, Method implementingMethod) {
		super(specMethod, implementingMethod, null);
	}

	public Method getImplementingMethod() {
		return super.getImplementingObject();
	}

	public Method getSpecMethod() {
		return super.getSpecObject();
	}

	private static String toString(Method specMethod, Method implementingMethod) {
		if (implementingMethod == null) {
			return missing(createString(specMethod));
		} else {
			return bestFitting(createString(implementingMethod), createString(specMethod));
		}
	}

	@Override
	public int getScore() {
		if (getImplementingObject() == null) {
			return 10;
		} else {
			return super.getScore();
		}
	}

	@Override
	public String getTitle() {
		Method specMethod = getSpecObject();
		if (isSuccess()) {
			return createString(specMethod);
		} else {
			return toString(specMethod, getImplementingObject());
		}
	}
}
