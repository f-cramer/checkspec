package checkspec.examples.example3;

/*-
 * #%L
 * CheckSpec Examples
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
import java.util.regex.Matcher;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.analysis.PackageAnalysis;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblemType;
import checkspec.specification.ClassSpecification;
import checkspec.type.MatchableType;

public class RegexPackageAnalysis extends PackageAnalysis {

	private static final String FORMAT = "package name should match pattern \"%s\"";

	@Override
	public Optional<ReportProblem> analyze(MatchableType actual, ClassSpecification specification, MultiValuedMap<Class<?>, Class<?>> oldReports) {
		Optional<RegexPackageSpecification> optional = specification.getExtension(RegexPackageSpecification.class);
		if (optional.isPresent()) {
			RegexPackageSpecification spec = optional.get();

			Package pkg = actual.getRawClass().getPackage();
			Matcher matcher = spec.getPackagePattern().matcher(pkg.getName());
			if (!matcher.matches()) {
				ReportProblem problem = new ReportProblem(1, String.format(FORMAT, spec.getPackagePattern().pattern()), ReportProblemType.ERROR);
				return Optional.of(problem);
			}
		} else {
			return super.analyze(actual, specification, oldReports);
		}

		return Optional.empty();
	}
}
