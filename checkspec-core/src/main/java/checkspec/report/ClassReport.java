package checkspec.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import checkspec.spec.ClassSpecification;
import checkspec.spec.ConstructorSpecification;
import checkspec.spring.ResolvableType;
import checkspec.util.ClassUtils;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class ClassReport extends Report<ClassSpecification, ResolvableType> {

	private List<FieldReport> fieldReports = new ArrayList<>();
	private List<ConstructorReport> constructorReports = new ArrayList<>();
	private List<MethodReport> methodReports = new ArrayList<>();

	public ClassReport(ClassSpecification spec, Class<?> implementation) {
		super(spec, ResolvableType.forClass(implementation), ClassUtils.toString(implementation));
	}

	@Override
	public List<Report<?, ?>> getSubReports() {
		List<Report<?, ?>> subReports = new ArrayList<>();
		subReports.addAll(fieldReports);
		subReports.addAll(constructorReports);
		subReports.addAll(methodReports);

		return Collections.unmodifiableList(subReports);
	}

	public void addFieldReport(FieldReport report) {
		fieldReports.add(report);
	}

	public void addFieldReports(Collection<FieldReport> reports) {
		fieldReports.addAll(reports);
	}

	public List<FieldReport> getFieldReports() {
		return Collections.unmodifiableList(fieldReports);
	}

	public void addConstructorReport(ConstructorReport report) {
		constructorReports.add(report);
	}

	public void addConstructorReports(Collection<ConstructorReport> reports) {
		this.constructorReports.addAll(reports);
	}

	public List<ConstructorReport> getConstructorReports() {
		return Collections.unmodifiableList(constructorReports);
	}

	public void addMethodReport(MethodReport report) {
		methodReports.add(report);
	}

	public void addMethodReports(Collection<MethodReport> reports) {
		methodReports.addAll(reports);
	}

	public List<MethodReport> getMethodReports() {
		return Collections.unmodifiableList(methodReports);
	}

	public boolean hasAnyImplementation() {
		List<Report<?, ?>> subReports = getSubReports();
		if (subReports.isEmpty() || subReports.parallelStream().anyMatch(e -> e.getImplementation() != null)) {
			return true;
		}

		if (subReports.size() == 1) {
			Report<?, ?> subReport = subReports.get(0);
			if (subReport instanceof ConstructorReport) {
				ConstructorSpecification constructorSpec = ((ConstructorReport) subReport).getSpec();
				return constructorSpec.getParameters().length != 0;
			}
		}

		return false;
	}
}
