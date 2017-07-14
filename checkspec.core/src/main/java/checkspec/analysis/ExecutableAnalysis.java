package checkspec.analysis;

import java.lang.reflect.Executable;
import java.util.Comparator;

import checkspec.report.Report;
import checkspec.specification.ExecutableSpecification;
import checkspec.specification.ParametersSpecification;
import checkspec.type.ResolvableType;

public abstract class ExecutableAnalysis<MemberType extends Executable, SpecificationType extends ExecutableSpecification<MemberType>, ReportType extends Report<MemberType, SpecificationType>>
		extends MemberAnalysis<MemberType, SpecificationType, ReportType> {

	protected static final ParametersAnalysis PARAMETERS_ANALYSIS = new ParametersAnalysis();

	private static final Comparator<ResolvableType> CLASS_NAME_COMPARATOR = Comparator.comparing(ResolvableType::getRawClass, Comparator.comparing(Class::getSimpleName));

	private final Comparator<ReportType> parameterComparator = (left, right) -> {
		ParametersSpecification leftParameters = left.getSpec().getParameters();
		ParametersSpecification rightParameters = right.getSpec().getParameters();
		int minLength = Math.min(leftParameters.getCount(), rightParameters.getCount());

		for (int i = 0; i < minLength; i++) {
			int comp = CLASS_NAME_COMPARATOR.compare(leftParameters.get(i).getType(), rightParameters.get(i).getType());
			if (comp != 0) {
				return comp;
			}
		}

		return Integer.compare(leftParameters.getCount(), rightParameters.getCount());
	};

	@Override
	protected Comparator<ReportType> getComparator() {
		Comparator<ReportType> comparator = super.getComparator();
		return comparator.thenComparing(parameterComparator);
	}
}
