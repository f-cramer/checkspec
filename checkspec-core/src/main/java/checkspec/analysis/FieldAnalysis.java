package checkspec.analysis;

import static checkspec.util.ClassUtils.getName;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import checkspec.report.ClassReport;
import checkspec.report.FieldReport;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblem.Type;
import checkspec.spec.ClassSpecification;
import checkspec.spec.FieldSpecification;
import checkspec.spring.ResolvableType;
import checkspec.util.ClassUtils;
import checkspec.util.FieldUtils;
import checkspec.util.MathUtils;
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
	protected Function<FieldSpecification, Stream<Pair<Field, FieldSpecification>>> getMapperFunction(Class<?> clazz) {
		return FieldSpec -> Arrays.stream(clazz.getDeclaredFields()).parallel()
				.map(Field -> Pair.of(Field, FieldSpec));
	}

	@Override
	protected int getDistance(Field field, FieldSpecification spec) {
		int nameDistance = NAME_SIMILARITY.apply(field.getName(), spec.getName());

		ResolvableType fieldType = FieldUtils.getType(field);
		int typeDistance = field.getType() == spec.getType().getRawClass() ? 0 : ClassUtils.isAssignable(spec.getType(), fieldType) ? 5 : 10;

		return MathUtils.multiplyWithoutOverflow(nameDistance, typeDistance);
	}

	@Override
	protected Field[] getMembers(Class<?> clazz) {
		return clazz.getDeclaredFields();
	}

	@Override
	protected FieldReport checkMember(Field field, FieldSpecification spec) {
		List<ReportProblem> problems = new ArrayList<>();

		String fieldName = field.getName();
		String specName = spec.getName();

		VISIBILITY_ANALYSIS.analyse(field, spec).ifPresent(problems::add);
		problems.addAll(MODIFIERS_ANALYSIS.analyse(field, spec));

		if (!fieldName.equals(specName)) {
			problems.add(new ReportProblem(1, String.format(NAME, specName), Type.WARNING));
		}

		ResolvableType fieldType = FieldUtils.getType(field);
		ResolvableType specType = spec.getType();

		if (fieldType.getRawClass() != specType.getRawClass()) {
			String fieldTypeName = getName(fieldType);
			String specTypeName = getName(specType);

			boolean compatible = ClassUtils.isAssignable(fieldType, specType);
			String format = compatible ? COMPATIBLE_TYPE : INCOMPATIBLE_TYPE;
			String message = String.format(format, fieldTypeName, specTypeName);
			problems.add(new ReportProblem(1, message, compatible ? Type.WARNING : Type.ERROR));
		}

		FieldReport report = new FieldReport(spec, field);
		report.addProblems(problems);
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
