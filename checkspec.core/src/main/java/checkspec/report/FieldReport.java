package checkspec.report;

import static checkspec.util.MessageUtils.*;

import java.lang.reflect.Field;

import checkspec.specification.FieldSpecification;
import checkspec.util.FieldUtils;

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
			return missing(FieldUtils.toString(specField));
		} else if (getType() == ReportType.SUCCESS) {
			return FieldUtils.toString(specField);
		} else {
			return bestFitting(FieldUtils.toString(getImplementation()), FieldUtils.toString(specField));
		}
	}

	@Override
	protected String getRawTypeName(Field raw) {
		return raw.getName();
	}
}
