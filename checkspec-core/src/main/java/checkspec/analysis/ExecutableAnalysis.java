package checkspec.analysis;

import java.lang.reflect.Executable;
import java.util.Comparator;

import checkspec.report.Report;
import checkspec.spec.ParametersSpecification;
import checkspec.spec.Specification;
import checkspec.spring.ResolvableType;

public abstract class ExecutableAnalysis<MemberType extends Executable, SpecificationType extends Specification<MemberType>, ReportType extends Report<SpecificationType, MemberType>>
		extends MemberAnalysis<MemberType, SpecificationType, ReportType> {

	protected static final ParametersAnalysis PARAMETERS_ANALYSIS = new ParametersAnalysis();

	private static Comparator<ResolvableType> CLASS_NAME_COMPARATOR = Comparator.comparing(ResolvableType::getRawClass, Comparator.comparing(Class::getSimpleName));

	private Comparator<ReportType> PARAMETER_COMPARATOR = (left, right) -> {
		ParametersSpecification leftParameters = getParametersSpecification(left.getSpec());
		ParametersSpecification rightParameters = getParametersSpecification(right.getSpec());
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
		return comparator.thenComparing(PARAMETER_COMPARATOR);
	}

	protected abstract ParametersSpecification getParametersSpecification(SpecificationType specification);
}
