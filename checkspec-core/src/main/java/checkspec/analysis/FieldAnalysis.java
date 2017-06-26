package checkspec.analysis;

import static checkspec.util.ClassUtils.getName;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Comparator;

import checkspec.report.ClassReport;
import checkspec.report.FieldReport;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblem.Type;
import checkspec.spec.ClassSpecification;
import checkspec.spec.FieldSpecification;
import checkspec.spring.ResolvableType;
import checkspec.util.ClassUtils;
import checkspec.util.FieldUtils;
import lombok.Getter;

@Getter
public class FieldAnalysis extends MemberAnalysis<Field, FieldSpecification, FieldReport> {

	private static final String NAME = "should have name \"%s\"";
	private static final String COMPATIBLE_TYPE = "has compatible type \"%s\" rather than \"%s\"";
	private static final String INCOMPATIBLE_TYPE = "has incompatible type \"%s\" rather than \"%s\"";

	private Comparator<FieldReport> comparator = Comparator.comparing(FieldReport::getSpec);

	@Override
	protected FieldSpecification[] getMemberSpecifications(ClassSpecification spec) {
		return spec.getFieldSpecifications();
	}

	@Override
	protected Field[] getMembers(Class<?> clazz) {
		return clazz.getDeclaredFields();
	}

	@Override
	protected FieldReport checkMember(Field field, FieldSpecification spec) {
		FieldReport report = new FieldReport(spec, field);

		String fieldName = field.getName();
		String specName = spec.getName();
		if (!fieldName.equals(specName)) {
			int score = calculateDistance(fieldName, specName);
			report.addProblem(new ReportProblem(score, String.format(NAME, specName), Type.WARNING));
		}

		VISIBILITY_ANALYSIS.analyze(field, spec).ifPresent(report::addProblem);
		report.addProblems(MODIFIERS_ANALYSIS.analyze(field, spec));

		ResolvableType fieldType = FieldUtils.getType(field);
		ResolvableType specType = spec.getType();

		if (fieldType.getRawClass() != specType.getRawClass()) {
			String fieldTypeName = getName(fieldType);
			String specTypeName = getName(specType);

			boolean compatible = ClassUtils.isAssignable(fieldType, specType);
			String format = compatible ? COMPATIBLE_TYPE : INCOMPATIBLE_TYPE;
			String message = String.format(format, fieldTypeName, specTypeName);
			report.addProblem(new ReportProblem(1, message, compatible ? Type.WARNING : Type.ERROR));
		}

		return report;
	}

	@Override
	protected FieldReport createEmptyReport(FieldSpecification specification) {
		return new FieldReport(specification);
	}

	@Override
	public void add(ClassReport report, Collection<? extends FieldReport> returnType) {
		report.addFieldReports(returnType);
	}
}
