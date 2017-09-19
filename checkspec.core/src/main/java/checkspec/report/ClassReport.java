package checkspec.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import checkspec.specification.ClassSpecification;
import checkspec.type.MatchableType;
import checkspec.util.ClassUtils;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class ClassReport extends Report<MatchableType, ClassSpecification> {

	private List<FieldReport> fieldReports = new ArrayList<>();
	private List<ConstructorReport> constructorReports = new ArrayList<>();
	private List<MethodReport> methodReports = new ArrayList<>();

	public ClassReport(ClassSpecification spec, Class<?> implementation) {
		super(spec, MatchableType.forClass(implementation), ClassUtils.toString(implementation));
	}

	@Override
	public List<Report<?, ?>> getSubReports() {
		List<Report<?, ?>> subReports = new ArrayList<>();
		subReports.addAll(fieldReports);
		subReports.addAll(constructorReports);
		subReports.addAll(methodReports);

		return Collections.unmodifiableList(subReports);
	}

	@Override
	public void removeSubReport(Report<?, ?> report) {
		if (report instanceof FieldReport) {
			fieldReports.remove(report);
		} else if (report instanceof ConstructorReport) {
			constructorReports.remove(report);
		} else if (report instanceof MethodReport) {
			methodReports.remove(report);
		}
	}

	public void addFieldReport(FieldReport report) {
		fieldReports.add(report);
	}

	public void addFieldReports(Collection<? extends FieldReport> reports) {
		fieldReports.addAll(reports);
	}

	public List<FieldReport> getFieldReports() {
		return Collections.unmodifiableList(fieldReports);
	}

	public void addConstructorReport(ConstructorReport report) {
		constructorReports.add(report);
	}

	public void addConstructorReports(Collection<? extends ConstructorReport> reports) {
		this.constructorReports.addAll(reports);
	}

	public List<ConstructorReport> getConstructorReports() {
		return Collections.unmodifiableList(constructorReports);
	}

	public void addMethodReport(MethodReport report) {
		methodReports.add(report);
	}

	public void addMethodReports(Collection<? extends MethodReport> reports) {
		methodReports.addAll(reports);
	}

	public List<MethodReport> getMethodReports() {
		return Collections.unmodifiableList(methodReports);
	}

	public boolean isAnyImplemenationMatching() {
		List<Report<?, ?>> subReports = getSubReports();
		if (subReports.size() == constructorReports.size()) {
			return true;
		}
		return subReports.isEmpty() || subReports.parallelStream().anyMatch(e -> e.isNameFitting());
	}

	@Override
	protected String getRawTypeName(MatchableType raw) {
		return ClassUtils.getName(raw);
	}
}
