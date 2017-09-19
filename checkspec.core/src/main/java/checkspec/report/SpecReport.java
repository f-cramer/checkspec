package checkspec.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import checkspec.specification.ClassSpecification;
import checkspec.util.ClassUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@EqualsAndHashCode
public class SpecReport {

	@NonNull
	private final ClassSpecification specification;

	@NonNull
	protected final List<ClassReport> classReports;

	public SpecReport(ClassSpecification specification, List<ClassReport> classReports) {
		this.specification = specification;
		this.classReports = new ArrayList<>(classReports);
	}

	public List<ClassReport> getClassReports() {
		return Collections.unmodifiableList(classReports);
	}

	public void removeClassReport(ClassReport report) {
		this.classReports.remove(report);
	}

	@Override
	public String toString() {
		return String.format("Reports for specification %s", ClassUtils.getName(specification.getRawElement()));
	}
}
