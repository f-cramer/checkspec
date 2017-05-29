package checkspec.report;

import java.util.List;

import javax.annotation.Nonnull;

import checkspec.spec.ClassSpecification;
import checkspec.util.ClassUtils;
import lombok.Value;

@Value
public class SpecReport {

	@Nonnull
	private final ClassSpecification spec;

	@Nonnull
	private final List<ClassReport> classReports;

	@Override
	public String toString() {
		return String.format("Reports for specification %s", ClassUtils.getName(spec.getRawElement()));
	}
}
