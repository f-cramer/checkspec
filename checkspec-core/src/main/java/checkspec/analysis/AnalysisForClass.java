package checkspec.analysis;

import checkspec.report.ClassReport;
import checkspec.spec.ClassSpecification;
import checkspec.spring.ResolvableType;

public interface AnalysisForClass<ReturnType> extends Analysis<ResolvableType, ClassSpecification, ReturnType> {

	void add(ClassReport report, ReturnType returnType);
}
