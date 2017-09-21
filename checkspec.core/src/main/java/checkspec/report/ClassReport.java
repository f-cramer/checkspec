package checkspec.report;

/*-
 * #%L
 * CheckSpec Core
 * %%
 * Copyright (C) 2017 Florian Cramer
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import checkspec.specification.ClassSpecification;
import checkspec.type.MatchableType;
import checkspec.util.ClassUtils;
import lombok.EqualsAndHashCode;

/**
 * A report for a class.
 *
 * @author Florian Cramer
 *
 */
@EqualsAndHashCode(callSuper = true)
public class ClassReport extends Report<MatchableType, ClassSpecification> {

	private List<FieldReport> fieldReports = new ArrayList<>();
	private List<ConstructorReport> constructorReports = new ArrayList<>();
	private List<MethodReport> methodReports = new ArrayList<>();

	/**
	 * Creates a new {@link ClassReport} from the given specification and
	 * implementation.
	 *
	 * @param spec
	 *            the specification
	 * @param implementation
	 *            the implementation
	 */
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

	/**
	 * Adds the given {@link FieldReport} to this report.
	 *
	 * @param report
	 *            the field report
	 */
	public void addFieldReport(FieldReport report) {
		fieldReports.add(report);
	}

	/**
	 * Adds the given {@link FieldReport}s to this report.
	 *
	 * @param reports
	 *            the field reports
	 */
	public void addFieldReports(Collection<? extends FieldReport> reports) {
		fieldReports.addAll(reports);
	}

	/**
	 * Returns the {@link FieldReport}s registered with this class report.
	 *
	 * @return the field reports registered with this class report
	 */
	public List<FieldReport> getFieldReports() {
		return Collections.unmodifiableList(fieldReports);
	}

	/**
	 * Adds the given {@link ConstructorReport} to this report.
	 *
	 * @param report
	 *            the constructor report
	 */
	public void addConstructorReport(ConstructorReport report) {
		constructorReports.add(report);
	}

	/**
	 * Adds the given {@link ConstructorReport}s to this report.
	 *
	 * @param reports
	 *            the constructor reports
	 */
	public void addConstructorReports(Collection<? extends ConstructorReport> reports) {
		this.constructorReports.addAll(reports);
	}

	/**
	 * Returns the {@link ConstructorReport}s registered with this class report.
	 *
	 * @return the constructor reports registered with this class report
	 */
	public List<ConstructorReport> getConstructorReports() {
		return Collections.unmodifiableList(constructorReports);
	}

	/**
	 * Adds the given {@link MethodReport} to this report.
	 *
	 * @param report
	 *            the method report
	 */
	public void addMethodReport(MethodReport report) {
		methodReports.add(report);
	}

	/**
	 * Adds the given {@link MethodReport}s to this report.
	 *
	 * @param reports
	 *            the method reports
	 */
	public void addMethodReports(Collection<? extends MethodReport> reports) {
		methodReports.addAll(reports);
	}

	/**
	 * Returns the {@link MethodReport}s registered with this class report.
	 *
	 * @return the method reports registered with this class report
	 */
	public List<MethodReport> getMethodReports() {
		return Collections.unmodifiableList(methodReports);
	}

	/**
	 * Returns whether or not any implementation registered with this report has
	 * a fitting name.
	 *
	 * @return whether or not any implementation registered with this report has
	 *         a fitting name
	 */
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
