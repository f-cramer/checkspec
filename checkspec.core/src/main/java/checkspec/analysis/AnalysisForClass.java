package checkspec.analysis;

import java.util.List;

import checkspec.report.ClassReport;
import checkspec.specification.ClassSpecification;
import checkspec.spring.ResolvableType;

public interface AnalysisForClass<ReturnType> extends Analysis<ResolvableType, ClassSpecification, ReturnType, List<ClassReport>> {

	void add(ClassReport report, ReturnType returnType);

	default int getPriority() {
		return Integer.MIN_VALUE;
	}
}
