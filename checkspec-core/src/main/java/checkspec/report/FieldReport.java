package checkspec.report;

import static checkspec.util.FieldUtils.createString;
import static checkspec.util.MessageUtils.bestFitting;
import static checkspec.util.MessageUtils.missing;

import java.lang.reflect.Field;

import checkspec.spec.FieldSpecification;

public class FieldReport extends Report<Field, FieldSpecification> {

	public FieldReport(FieldSpecification specField) {
		super(specField);
	}

	public FieldReport(FieldSpecification specField, Field implementingField) {
		super(specField, implementingField);
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

	@Override
	protected String getRawTypeName(Field raw) {
		return raw.getName();
	}
}
