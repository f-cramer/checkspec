package checkspec.analysis;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.report.ClassReport;
import checkspec.specification.ClassSpecification;
import checkspec.type.ResolvableType;

public interface ClassAnalysis<ReturnType> extends Analysis<ResolvableType, ClassSpecification, ReturnType, MultiValuedMap<Class<?>, Class<?>>> {

	void add(ClassReport report, ReturnType returnType);

	default int getPriority() {
		return Integer.MIN_VALUE;
	}
}
