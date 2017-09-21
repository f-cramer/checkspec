package checkspec.analysis;

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

import java.util.Optional;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.report.ClassReport;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblemType;
import checkspec.specification.ClassSpecification;
import checkspec.specification.PackageSpecification;
import checkspec.type.MatchableType;

public class PackageAnalysis implements ClassAnalysis<Optional<ReportProblem>> {

	private static final String FORMAT = "should live in package \"%s\"";

	@Override
	public Optional<ReportProblem> analyze(MatchableType actual, ClassSpecification specification, MultiValuedMap<Class<?>, Class<?>> oldReports) {
		PackageSpecification packageSpecification = specification.getPackage();
		String packageName = packageSpecification.getName();

		String actualPackageName = actual.getRawClass().getPackage().getName();
		if (!packageName.equals(actualPackageName)) {
			ReportProblem problem = new ReportProblem(1, String.format(FORMAT, packageName), ReportProblemType.ERROR);
			return Optional.of(problem);
		}

		return Optional.empty();
	}

	@Override
	public void add(ClassReport report, Optional<ReportProblem> returnType) {
		returnType.ifPresent(report::addProblem);
	}
}
