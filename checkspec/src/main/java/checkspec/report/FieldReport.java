package checkspec.report;

import static checkspec.util.FieldUtils.createString;
import static checkspec.util.MessageUtils.bestFitting;
import static checkspec.util.MessageUtils.missing;

import java.lang.reflect.Field;

import checkspec.spec.FieldSpec;

public class FieldReport extends Report<FieldSpec, Field> {

	public FieldReport(FieldSpec specField) {
		super(specField, null, null);
	}

	public FieldReport(FieldSpec specField, Field implementingField) {
		super(specField, implementingField, null);
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
		if (getImplementation() == null) {
			return missing(createString(specField));
		} else if (getType() == ProblemType.SUCCESS) {
			return createString(specField);
		} else {
			return bestFitting(createString(getImplementation()), createString(specField));
		}
	}
}
