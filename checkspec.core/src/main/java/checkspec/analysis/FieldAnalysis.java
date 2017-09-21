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

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.BinaryOperator;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.report.ClassReport;
import checkspec.report.FieldReport;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblemType;
import checkspec.specification.ClassSpecification;
import checkspec.specification.FieldSpecification;
import checkspec.type.MatchableType;
import checkspec.util.FieldUtils;
import lombok.Getter;

@Getter
public class FieldAnalysis extends MemberAnalysis<Field, FieldSpecification, FieldReport> {

	private static final String NAME = "should have name \"%s\"";
	private static final String COMPATIBLE_TYPE = "has compatible type \"%s\" rather than \"%s\"";
	private static final String INCOMPATIBLE_TYPE = "has incompatible type \"%s\" rather than \"%s\"";

	private Comparator<FieldReport> comparator = Comparator.comparing(FieldReport::getSpecification);

	@Override
	protected FieldSpecification[] getMemberSpecifications(ClassSpecification spec) {
		return spec.getFieldSpecifications();
	}

	@Override
	protected Field[] getMembers(Class<?> clazz) {
		return clazz.getDeclaredFields();
	}

	@Override
	protected FieldReport checkMember(Field field, FieldSpecification spec, MultiValuedMap<Class<?>, Class<?>> matches) {
		FieldReport report = new FieldReport(spec, field);

		String fieldName = field.getName();
		String specName = spec.getName();
		if (!fieldName.equals(specName)) {
			int score = calculateDistance(fieldName, specName);
			report.addProblem(new ReportProblem(score, String.format(NAME, specName), ReportProblemType.WARNING));
		}

		VISIBILITY_ANALYSIS.analyze(field, spec).ifPresent(report::addProblem);
		report.addProblems(MODIFIERS_ANALYSIS.analyze(field, spec));

		MatchableType fieldType = FieldUtils.getType(field);
		MatchableType specType = spec.getType();

		BinaryOperator<String> compatibleString = (s, a) -> String.format(COMPATIBLE_TYPE, s, a);
		BinaryOperator<String> incompatibleString = (a, s) -> String.format(INCOMPATIBLE_TYPE, a, s);
		AnalysisUtils.compareTypes(specType, fieldType, matches, compatibleString, incompatibleString)
				.ifPresent(report::addProblem);

		return report;
	}

	@Override
	protected FieldReport createEmptyReport(FieldSpecification specification) {
		return new FieldReport(specification);
	}

	@Override
	public void add(ClassReport report, Collection<? extends FieldReport> returnType) {
		report.addFieldReports(returnType);
	}
}
