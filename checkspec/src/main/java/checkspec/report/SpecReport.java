package checkspec.report;

import java.util.List;

import checkspec.spec.ClassSpecification;
import checkspec.util.ClassUtils;
import lombok.NonNull;
import lombok.Value;

@Value
public class SpecReport {

	@NonNull
	private final ClassSpecification spec;

	@NonNull
	private final List<ClassReport> classReports;

	@Override
	public String toString() {
		return String.format("Reports for specification %s", ClassUtils.getName(spec.getRawElement()));
	}
}
