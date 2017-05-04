package checkspec.report;

import static checkspec.util.FieldUtils.createString;
import static checkspec.util.MessageUtils.bestFitting;
import static checkspec.util.MessageUtils.missing;

import java.lang.reflect.Field;

import checkspec.type.FieldSpec;

public class FieldReport extends Report<FieldSpec, Field> {

	public FieldReport(FieldSpec specField) {
		super(specField, null, null);
	}

	public FieldReport(FieldSpec specField, Field implementingField) {
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
		if (getImplementation() == null) {
			return 1;
		} else {
			return super.getScore();
		}
	}

	@Override
	public String getTitle() {
		Field specField = getSpec().getRawElement();
		if (isSuccess()) {
			return createString(specField);
		} else {
			return toString(specField, getImplementation());
		}
	}
}
