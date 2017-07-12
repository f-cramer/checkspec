package checkspec.analysis;

import java.util.Map;

import checkspec.report.ClassReport;
import checkspec.specification.ClassSpecification;
import checkspec.spring.ResolvableType;

public interface AnalysisForClass<ReturnType> extends Analysis<ResolvableType, ClassSpecification, ReturnType, Map<ClassSpecification, ClassReport>> {

	void add(ClassReport report, ReturnType returnType);

	default int getPriority() {
		return Integer.MIN_VALUE;
	}
}
