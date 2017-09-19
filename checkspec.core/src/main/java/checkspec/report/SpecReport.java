package checkspec.report;

import java.util.Collections;
import java.util.List;

import checkspec.specification.ClassSpecification;
import checkspec.util.ClassUtils;
import lombok.NonNull;
import lombok.Value;

@Value
public class SpecReport {

	@NonNull
	private final ClassSpecification specification;

	@NonNull
	private final List<ClassReport> classReports;

	public List<ClassReport> getClassReports() {
		return Collections.unmodifiableList(classReports);
	}

	@Override
	public String toString() {
		return String.format("Reports for specification %s", ClassUtils.getName(specification.getRawElement()));
	}
}
