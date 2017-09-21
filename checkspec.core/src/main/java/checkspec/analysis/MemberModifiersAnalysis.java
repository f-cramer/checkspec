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

import java.lang.reflect.Member;
import java.util.List;

import checkspec.report.ReportProblem;
import checkspec.specification.MemberSpecification;

/**
 * Analyzes the modifiers of members of a class like fields, constructors and
 * methods.
 *
 * @author Florian Cramer
 *
 */
public class MemberModifiersAnalysis extends AbstractModifiersAnalysis implements AnalysisWithoutPayload<Member, MemberSpecification<? extends Member>, List<ReportProblem>> {

	@Override
	public List<ReportProblem> analyze(Member actual, MemberSpecification<? extends Member> specification, Void payload) {
		boolean checkAbstract = !specification.getRawElement().getDeclaringClass().isInterface() || actual.getDeclaringClass().isInterface();
		return analyzeModifiers(actual.getModifiers(), specification.getModifiers(), checkAbstract);
	}

}
