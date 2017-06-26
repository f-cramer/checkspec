package checkspec.report;

import static checkspec.util.FieldUtils.createString;
import static checkspec.util.MessageUtils.bestFitting;
import static checkspec.util.MessageUtils.missing;

import java.lang.reflect.Field;

import checkspec.spec.FieldSpecification;

public class FieldReport extends Report<FieldSpecification, Field> {

	public FieldReport(FieldSpecification specField) {
		super(specField, null, null);
	}

	public FieldReport(FieldSpecification specField, Field implementingField) {
		super(specField, implementingField, null);
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
