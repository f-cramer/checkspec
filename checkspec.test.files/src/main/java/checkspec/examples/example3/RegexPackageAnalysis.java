package checkspec.examples.example3;

import java.util.Optional;
import java.util.regex.Matcher;

import checkspec.analysis.PackageAnalysis;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblemType;
import checkspec.specification.ClassSpecification;
import checkspec.spring.ResolvableType;

public class RegexPackageAnalysis extends PackageAnalysis {

	private static final String FORMAT = "package name should match pattern \"%s\"";

	@Override
	public Optional<ReportProblem> analyze(ResolvableType actual, ClassSpecification specification) {
		Optional<RegexPackageSpecification> optional = specification.getExtension(RegexPackageSpecification.class);
		if (optional.isPresent()) {
			RegexPackageSpecification spec = optional.get();

			Package pkg = actual.getRawClass().getPackage();
			Matcher matcher = spec.getPackagePattern().matcher(pkg.getName());
			if (!matcher.matches()) {
				ReportProblem problem = new ReportProblem(1, String.format(FORMAT, spec.getPackagePattern().pattern()), ReportProblemType.ERROR);
				return Optional.of(problem);
			}
		} else {
			return super.analyze(actual, specification);
		}

		return Optional.empty();
	}
}
