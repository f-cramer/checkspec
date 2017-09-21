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
