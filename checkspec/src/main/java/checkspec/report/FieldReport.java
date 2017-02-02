package checkspec.report;

import static checkspec.util.FieldUtils.createString;

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
			return String.format("implementation for \"%s\" is missing", createString(specField));
		} else {
			return String.format("\"%s\" is best fitting for \"%s\"", createString(implementingField), createString(specField));
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
