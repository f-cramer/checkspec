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

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.report.ParametersReport;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblemType;
import checkspec.specification.ParameterSpecification;
import checkspec.specification.ParametersSpecification;
import checkspec.type.MatchableType;
import checkspec.util.ClassUtils;

/**
 * Analyzes the parameters of a constructor or a method.
 *
 * @author Florian Cramer
 *
 */
public class ParametersAnalysis implements Analysis<Parameter[], ParametersSpecification, ParametersReport, MultiValuedMap<Class<?>, Class<?>>> {

	private static final String ADDED = "added parameter of type \"%s\" on index %d";
	private static final String DELETED = "removed parameter of type \"%s\" from index %d";
	private static final String SUBSTITUTE_COMPATIBLE = "parameter at index %d has compatible type \"%s\"";
	private static final String SUBSTITUTE_INCOMPATIBLE = "parameter at index %d has incompatible type \"%s\"";

	@Override
	public ParametersReport analyze(Parameter[] actual, ParametersSpecification specification, MultiValuedMap<Class<?>, Class<?>> oldReports) {
		ParametersReport report = new ParametersReport(specification, Arrays.asList(actual));

		int specParameterCount = specification.getCount();

		MatchableType[] specClasses = IntStream.range(0, specParameterCount).mapToObj(specification::get).map(ParameterSpecification::getType).toArray(MatchableType[]::new);
		MatchableType[] actualClasses = Arrays.stream(actual).parallel().map(ParametersAnalysis::getType).toArray(MatchableType[]::new);

		report.addProblems(calculateDistance(specClasses, actualClasses, oldReports));

		return report;
	}

	private static MatchableType getType(Parameter parameter) {
		Executable executable = parameter.getDeclaringExecutable();
		OptionalInt optionalIndex = findIndex(parameter);

		if (optionalIndex.isPresent()) {
			int index = optionalIndex.getAsInt();

			if (executable instanceof Constructor<?>) {
				return MatchableType.forConstructorParameter((Constructor<?>) executable, index);
			} else if (executable instanceof Method) {
				return MatchableType.forMethodParameter((Method) executable, index);
			}
		}

		return MatchableType.forType(parameter.getParameterizedType());
	}

	private static OptionalInt findIndex(Parameter parameter) {
		Executable executable = parameter.getDeclaringExecutable();
		return IntStream.range(0, executable.getParameterCount()).filter(index -> executable.getParameters()[index] == parameter).findFirst();
	}

	private static List<ReportProblem> calculateDistance(MatchableType[] left, MatchableType[] right, MultiValuedMap<Class<?>, Class<?>> oldReports) {
		if (left.length == 0) {
			return Collections.emptyList();
		} else if (right.length == 0) {
			return Collections.emptyList();
		}
		int[] previousCosts = new int[left.length + 1];
		int[] costs = new int[left.length + 1];
		int[] tempD;
		final int[][] matrix = new int[right.length + 1][left.length + 1];

		for (int index = 0; index <= left.length; index++) {
			matrix[0][index] = index;
		}
		for (int index = 0; index <= right.length; index++) {
			matrix[index][0] = index;
		}

		MatchableType rightJ;

		int cost;
		for (int i = 0; i <= left.length; i++) {
			previousCosts[i] = i;
		}

		for (int j = 1; j <= right.length; j++) {
			rightJ = right[j - 1];
			costs[0] = j;

			for (int i = 1; i <= left.length; i++) {
				cost = left[i - 1].matches(rightJ, oldReports).evaluate(0, 1, 2);
				costs[i] = Math.min(Math.min(costs[i - 1] + 1, previousCosts[i] + 1), previousCosts[i - 1] + cost);
				matrix[j][i] = costs[i];
			}

			tempD = previousCosts;
			previousCosts = costs;
			costs = tempD;
		}
		return findDetailedResults(left, right, matrix, oldReports);
	}

	private static List<ReportProblem> findDetailedResults(final MatchableType[] left, final MatchableType[] right, final int[][] matrix, MultiValuedMap<Class<?>, Class<?>> oldReports) {
		List<ReportProblem> problems = new ArrayList<>();

		int rowIndex = right.length;
		int columnIndex = left.length;

		int dataAtLeft = 0;
		int dataAtTop = 0;
		int dataAtDiagonal = 0;
		int data = 0;
		boolean deleted = false;
		boolean added = false;

		while (rowIndex >= 0 && columnIndex >= 0) {

			if (columnIndex == 0) {
				dataAtLeft = -1;
			} else {
				dataAtLeft = matrix[rowIndex][columnIndex - 1];
			}
			if (rowIndex == 0) {
				dataAtTop = -1;
			} else {
				dataAtTop = matrix[rowIndex - 1][columnIndex];
			}
			if (rowIndex > 0 && columnIndex > 0) {
				dataAtDiagonal = matrix[rowIndex - 1][columnIndex - 1];
			} else {
				dataAtDiagonal = -1;
			}
			if (dataAtLeft == -1 && dataAtTop == -1 && dataAtDiagonal == -1) {
				break;
			}
			data = matrix[rowIndex][columnIndex];

			MatchableType curLeft = columnIndex > 0 ? left[columnIndex - 1] : null;
			MatchableType curRight = rowIndex > 0 ? right[rowIndex - 1] : null;
			if (columnIndex > 0 && rowIndex > 0 && curLeft.equals(curRight)) {
				columnIndex--;
				rowIndex--;
				continue;
			}

			String leftName = curLeft == null ? null : ClassUtils.getName(curLeft);
			String rightName = curRight == null ? null : ClassUtils.getName(curRight);

			deleted = false;
			added = false;
			if (data - 1 == dataAtLeft && (data <= dataAtDiagonal && data <= dataAtTop) || (dataAtDiagonal == -1 && dataAtTop == -1)) { // NOPMD
				problems.add(0, new ReportProblem(3, String.format(DELETED, leftName, rowIndex - 1), ReportProblemType.ERROR));
				deleted = true;
				columnIndex--;
			} else if (data - 1 == dataAtTop && (data <= dataAtDiagonal && data <= dataAtLeft) || (dataAtDiagonal == -1 && dataAtLeft == -1)) { // NOPMD
				problems.add(0, new ReportProblem(3, String.format(ADDED, rightName, rowIndex - 1), ReportProblemType.ERROR));
				added = true;
				rowIndex--;
			}

			if (!added && !deleted) {
				int index = columnIndex;
				AnalysisUtils.compareTypes(curLeft, curRight, oldReports, (s, a) -> String.format(SUBSTITUTE_COMPATIBLE, index - 1, rightName),
						(s, a) -> String.format(SUBSTITUTE_INCOMPATIBLE, index - 1, rightName)).ifPresent(problems::add);
				columnIndex--;
				rowIndex--;
			}
		}

		return problems;
	}
}
