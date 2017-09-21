package checkspec.report;

import static checkspec.util.MessageUtils.*;

import java.lang.reflect.Field;

import checkspec.specification.FieldSpecification;
import checkspec.util.FieldUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class FieldReport extends Report<Field, FieldSpecification> {

	public FieldReport(FieldSpecification specField) {
		super(specField);
	}

	public FieldReport(FieldSpecification specField, Field implementingField) {
		super(specField, implementingField);
	}

	@Override
	public String getTitle() {
		Field specField = getSpecification().getRawElement();
		if (getImplementation() == null) {
			return missing(FieldUtils.createString(specField));
		} else if (getType() == ReportType.SUCCESS) {
			return FieldUtils.createString(specField);
		} else {
			return bestFitting(FieldUtils.createString(getImplementation()), FieldUtils.createString(specField));
		}
	}

	@Override
	protected String getRawTypeName(Field raw) {
		return raw.getName();
	}
}
