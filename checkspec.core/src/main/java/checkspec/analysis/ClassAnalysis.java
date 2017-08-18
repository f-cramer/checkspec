package checkspec.analysis;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.report.ClassReport;
import checkspec.specification.ClassSpecification;
import checkspec.type.MatchableType;

public interface ClassAnalysis<ReturnType> extends Analysis<MatchableType, ClassSpecification, ReturnType, MultiValuedMap<Class<?>, Class<?>>> {

	void add(ClassReport report, ReturnType returnType);

	default int getPriority() {
		return Integer.MIN_VALUE;
	}
}
