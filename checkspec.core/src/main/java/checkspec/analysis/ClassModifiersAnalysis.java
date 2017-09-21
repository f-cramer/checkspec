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



import java.util.List;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.report.ClassReport;
import checkspec.report.ReportProblem;
import checkspec.specification.ClassSpecification;
import checkspec.specification.ModifiersSpecification;
import checkspec.type.MatchableType;

/**
 * Analyzes the modifiers of a class.
 *
 * @author Florian Cramer
 *
 */
public class ClassModifiersAnalysis extends AbstractModifiersAnalysis implements ClassAnalysis<List<ReportProblem>> {

	@Override
	public List<ReportProblem> analyze(MatchableType actual, ClassSpecification spec, MultiValuedMap<Class<?>, Class<?>> oldReports) {
		ModifiersSpecification modifiersSpec = spec.getModifiers();
		return analyzeModifiers(actual.getRawClass().getModifiers(), modifiersSpec, !modifiersSpec.isInterface() || actual.getRawClass().isInterface());
	}

	@Override
	public void add(ClassReport report, List<ReportProblem> returnType) {
		report.addProblems(returnType);
	}
}
