package checkspec.report;

import static checkspec.util.FieldUtils.createString;
import static checkspec.util.MessageUtils.bestFitting;
import static checkspec.util.MessageUtils.missing;

import java.lang.reflect.Field;

public class FieldReport extends Report<Field> {

	public FieldReport(Field specField) {
		super(specField, null, null);
	}

	public FieldReport(Field specField, Field implementingField) {
		super(specField, implementingField, null);
	}

	private static String toString(Field specField, Field implementingField) {
		if (implementingField == null) {
			return missing(createString(specField));
		} else {
			return bestFitting(createString(implementingField), createString(specField));
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
		Field specField = getSpecObject();
		if (isSuccess()) {
			return createString(specField);
		} else {
			return toString(specField, getImplementingObject());
		}
	}
}
