package checkspec.analysis;

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

import checkspec.report.ParametersReport;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblem.Type;
import checkspec.spec.ParameterSpecification;
import checkspec.spec.ParametersSpecification;
import checkspec.spring.ResolvableType;
import checkspec.util.ClassUtils;

public class ParametersAnalysis implements Analysis<Parameter[], ParametersSpecification, ParametersReport> {

	private static final String ADDED = "added parameter of type \"%s\" on index %d";
	private static final String DELETED = "removed parameter of type \"%s\" from index %d";
	private static final String SUBSTITUTE_COMPATIBLE = "parameter at index %d has compatible type \"%s\"";
	private static final String SUBSTITUTE_INCOMPATIBLE = "parameter at index %d has incompatible type \"%s\"";

	@Override
	public ParametersReport analyze(Parameter[] actual, ParametersSpecification specification) {
		ParametersReport report = new ParametersReport(specification, actual);

		int specParameterCount = specification.getCount();

		ResolvableType[] specClasses = IntStream.range(0, specParameterCount)
				.mapToObj(specification::get)
				.map(ParameterSpecification::getType)
				.toArray(ResolvableType[]::new);
		ResolvableType[] actualClasses = Arrays.stream(actual).parallel()
				.map(ParametersAnalysis::getType)
				.toArray(ResolvableType[]::new);

		report.addProblems(calculateDistance(specClasses, actualClasses));

		return report;
	}

	private static ResolvableType getType(Parameter parameter) {
		Executable executable = parameter.getDeclaringExecutable();
		OptionalInt optionalIndex = findIndex(parameter);

		if (optionalIndex.isPresent()) {
			int index = optionalIndex.getAsInt();

			if (executable instanceof Constructor<?>) {
				return ResolvableType.forConstructorParameter((Constructor<?>) executable, index);
			} else if (executable instanceof Method) {
				return ResolvableType.forMethodParameter((Method) executable, index);
			}
		}

		return ResolvableType.forType(parameter.getParameterizedType());
	}

	private static OptionalInt findIndex(Parameter parameter) {
		Executable executable = parameter.getDeclaringExecutable();
		return IntStream.range(0, executable.getParameterCount()).filter(index -> executable.getParameters()[index] == parameter).findFirst();
	}

	private static List<ReportProblem> calculateDistance(ResolvableType[] left, ResolvableType[] right) {
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

		ResolvableType rightJ;

		int cost;
		for (int i = 0; i <= left.length; i++) {
			previousCosts[i] = i;
		}

		for (int j = 1; j <= right.length; j++) {
			rightJ = right[j - 1];
			costs[0] = j;

			for (int i = 1; i <= left.length; i++) {
				cost = ClassUtils.equal(left[i - 1], rightJ) ? 0 : ClassUtils.isAssignable(left[i - 1], rightJ) ? 1 : 2;
				costs[i] = Math.min(Math.min(costs[i - 1] + 1, previousCosts[i] + 1), previousCosts[i - 1] + cost);
				matrix[j][i] = costs[i];
			}

			tempD = previousCosts;
			previousCosts = costs;
			costs = tempD;
		}
		return findDetailedResults(left, right, matrix);
	}

	private static List<ReportProblem> findDetailedResults(final ResolvableType[] left, final ResolvableType[] right, final int[][] matrix) {
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

			ResolvableType curLeft = columnIndex > 0 ? left[columnIndex - 1] : null;
			ResolvableType curRight = rowIndex > 0 ? right[rowIndex - 1] : null;
			if (columnIndex > 0 && rowIndex > 0 && ClassUtils.equal(curLeft, curRight)) {
				columnIndex--;
				rowIndex--;
				continue;
			}

			String leftName = curLeft == null ? null : ClassUtils.getName(curLeft);
			String rightName = curRight == null ? null : ClassUtils.getName(curRight);

			deleted = false;
			added = false;
			if (data - 1 == dataAtLeft && (data <= dataAtDiagonal && data <= dataAtTop) || (dataAtDiagonal == -1 && dataAtTop == -1)) { // NOPMD
				problems.add(0, new ReportProblem(3, String.format(DELETED, leftName, rowIndex - 1), Type.ERROR));
				deleted = true;
				columnIndex--;
			} else if (data - 1 == dataAtTop && (data <= dataAtDiagonal && data <= dataAtLeft) || (dataAtDiagonal == -1 && dataAtLeft == -1)) { // NOPMD
				problems.add(0, new ReportProblem(3, String.format(ADDED, rightName, rowIndex - 1), Type.ERROR));
				added = true;
				rowIndex--;
			}

			if (!added && !deleted) {
				if (ClassUtils.isAssignable(curLeft, curRight)) {
					problems.add(0, new ReportProblem(1, String.format(SUBSTITUTE_COMPATIBLE, columnIndex - 1, rightName), Type.WARNING));
				} else {
					problems.add(0, new ReportProblem(2, String.format(SUBSTITUTE_INCOMPATIBLE, columnIndex - 1, rightName), Type.ERROR));
				}
				columnIndex--;
				rowIndex--;
			}
		}

		return problems;
	}
}
