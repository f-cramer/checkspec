package checkspec.analysis;

import java.util.Optional;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.report.ClassReport;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblemType;
import checkspec.specification.ClassSpecification;
import checkspec.specification.PackageSpecification;
import checkspec.type.ResolvableType;

public class PackageAnalysis implements ClassAnalysis<Optional<ReportProblem>> {

	private static final String FORMAT = "should live in package \"%s\"";

	@Override
	public Optional<ReportProblem> analyze(ResolvableType actual, ClassSpecification specification, MultiValuedMap<Class<?>, Class<?>> oldReports) {
		PackageSpecification packageSpecification = specification.getPackage();
		String packageName = packageSpecification.getName();

		String actualPackageName = actual.getRawClass().getPackage().getName();
		if (!packageName.equals(actualPackageName)) {
			ReportProblem problem = new ReportProblem(1, String.format(FORMAT, packageName), ReportProblemType.ERROR);
			return Optional.of(problem);
		}

		return Optional.empty();
	}

	@Override
	public void add(ClassReport report, Optional<ReportProblem> returnType) {
		returnType.ifPresent(report::addProblem);
	}
}
