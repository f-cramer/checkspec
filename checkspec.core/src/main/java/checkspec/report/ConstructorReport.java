package checkspec.report;

import static checkspec.util.ConstructorUtils.*;
import static checkspec.util.MessageUtils.*;

import java.lang.reflect.Constructor;

import checkspec.specification.ConstructorSpecification;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ConstructorReport extends ExecutableReport<Constructor<?>, ConstructorSpecification> {

	public ConstructorReport(ConstructorSpecification specification) {
		super(specification);
	}

	public ConstructorReport(ConstructorSpecification specification, Constructor<?> constructor, ParametersReport parametersReport) {
		super(specification, constructor, parametersReport);
	}

	@Override
	public String getTitle() {
		Constructor<?> specConstructor = getSpecification().getRawElement();
		if (getImplementation() == null) {
			return missing(createString(specConstructor));
		} else if (getType() == ReportType.SUCCESS) {
			return createString(specConstructor);
		} else {
			return bestFitting(createString(getImplementation()), createString(specConstructor));
		}
	}

	@Override
	protected String getRawTypeName(Constructor<?> raw) {
		return "";
	}
}
