package checkspec.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import checkspec.spring.ResolvableType;
import checkspec.util.ClassUtils;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class ClassReport extends Report<Class<?>> {

	private List<FieldReport> fieldReports = new ArrayList<>();
	private List<ConstructorReport> constructorReports = new ArrayList<>();
	private List<MethodReport> methodReports = new ArrayList<>();

	public ClassReport(Class<?> specClass, Class<?> implementingClass) {
		super(specClass, implementingClass, String.format("%s %s %s", ClassUtils.getVisibility(implementingClass),
				ClassUtils.getType(implementingClass), ClassUtils.getName(implementingClass)));
	}

	@Override
	public List<Report<?>> getSubReports() {
		List<Report<?>> subReports = new ArrayList<>();
		subReports.addAll(fieldReports);
		subReports.addAll(constructorReports);
		subReports.addAll(methodReports);

		return Collections.unmodifiableList(subReports);
	}

	public List<FieldReport> getFieldReports() {
		return Collections.unmodifiableList(fieldReports);
	}

	public void add(FieldReport report) {
		fieldReports.add(report);
	}

	public List<ConstructorReport> getConstructorReports() {
		return Collections.unmodifiableList(constructorReports);
	}

	public void add(ConstructorReport report) {
		constructorReports.add(report);
	}

	public List<MethodReport> getMethodReports() {
		return Collections.unmodifiableList(methodReports);
	}

	public void add(MethodReport report) {
		methodReports.add(report);
	}

	public Class<?> getSpecClass() {
		return super.getSpecObject();
	}

	public Class<?> getImplementingClass() {
		return super.getImplementingObject();
	}

	public boolean hasAnyImplementation() {
		List<Report<?>> subReports = getSubReports();
		return subReports.isEmpty() || subReports.parallelStream().anyMatch(e -> e.getImplementingObject() != null);
	}

	@Override
	protected void addSubReport(Report<?> report) {
		if (report instanceof FieldReport) {
			add((FieldReport) report);
		} else if (report instanceof ConstructorReport) {
			add((ConstructorReport) report);
		} else if (report instanceof MethodReport) {
			add((MethodReport) report);
		} else {
			ResolvableType type = ResolvableType.forClass(report.getClass());
			throw new IllegalArgumentException(String.format("cannot add report of type \"%s\" to class report", type));
		}
	}
}
