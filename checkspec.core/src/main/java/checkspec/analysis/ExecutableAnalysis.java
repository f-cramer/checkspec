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

import java.lang.reflect.Executable;
import java.util.Comparator;

import checkspec.report.Report;
import checkspec.specification.ExecutableSpecification;
import checkspec.specification.ParametersSpecification;
import checkspec.type.MatchableType;

public abstract class ExecutableAnalysis<MemberType extends Executable, SpecificationType extends ExecutableSpecification<MemberType>, ReportType extends Report<MemberType, SpecificationType>>
		extends MemberAnalysis<MemberType, SpecificationType, ReportType> {

	protected static final ParametersAnalysis PARAMETERS_ANALYSIS = new ParametersAnalysis();
	protected static final ExceptionsAnalysis EXCEPTION_ANALYSIS = new ExceptionsAnalysis();

	private static final Comparator<MatchableType> CLASS_NAME_COMPARATOR = Comparator.comparing(MatchableType::getRawClass, Comparator.comparing(Class::getSimpleName));

	private final Comparator<ReportType> parameterComparator = (left, right) -> {
		ParametersSpecification leftParameters = left.getSpecification().getParameters();
		ParametersSpecification rightParameters = right.getSpecification().getParameters();
		int minLength = Math.min(leftParameters.getCount(), rightParameters.getCount());

		for (int i = 0; i < minLength; i++) {
			int comp = CLASS_NAME_COMPARATOR.compare(leftParameters.get(i).getType(), rightParameters.get(i).getType());
			if (comp != 0) {
				return comp;
			}
		}

		return Integer.compare(leftParameters.getCount(), rightParameters.getCount());
	};

	@Override
	protected Comparator<ReportType> getComparator() {
		Comparator<ReportType> comparator = super.getComparator();
		return comparator.thenComparing(parameterComparator);
	}
}
