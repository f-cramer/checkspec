package checkspec.analysis;

import checkspec.report.ClassReport;
import checkspec.specification.ClassSpecification;
import checkspec.spring.ResolvableType;

public interface AnalysisForClass<ReturnType> extends Analysis<ResolvableType, ClassSpecification, ReturnType> {

	void add(ClassReport report, ReturnType returnType);

	default int getPriority() {
		return Integer.MIN_VALUE;
	}
}
