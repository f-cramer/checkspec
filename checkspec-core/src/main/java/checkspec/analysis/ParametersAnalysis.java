package checkspec.analysis;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
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
	private static final String SUBSTITUTE = "parameter at index %d should be of type \"%s\" rather than \"%s\"";

	@Override
	public ParametersReport analyse(Parameter[] actual, ParametersSpecification specification) {
		ParametersReport report = new ParametersReport(specification, actual);

		int specParameterCount = specification.getCount();

		ResolvableType[] actualClasses = Arrays.stream(actual).parallel()
				.map(ParametersAnalysis::getType)
				.toArray(ResolvableType[]::new);
		ResolvableType[] specClasses = IntStream.range(0, specParameterCount)
				.mapToObj(specification::get)
				.map(ParameterSpecification::getType)
				.toArray(ResolvableType[]::new);
		
		report.addProblems(unlimitedCompare(actualClasses, specClasses));

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

	private static List<ReportProblem> unlimitedCompare(ResolvableType[] left, ResolvableType[] right) {
		int n = left.length;
		int m = right.length;

		if (n == 0) {
			return Collections.emptyList();
		} else if (m == 0) {
			return Collections.emptyList();
		}
		boolean swapped = false;
		if (n > m) {
			// swap the input strings to consume less memory
			final ResolvableType[] tmp = left;
			left = right;
			right = tmp;
			n = m;
			m = right.length;
			swapped = true;
		}

		int[] p = new int[n + 1]; // 'previous' cost array, horizontally
		int[] d = new int[n + 1]; // cost array, horizontally
		int[] tempD; // placeholder to assist in swapping p and d
		final int[][] matrix = new int[m + 1][n + 1];

		// filling the first row and first column values in the matrix
		for (int index = 0; index <= n; index++) {
			matrix[0][index] = index;
		}
		for (int index = 0; index <= m; index++) {
			matrix[index][0] = index;
		}

		// indexes into strings left and right
		int i; // iterates through left
		int j; // iterates through right

		ResolvableType rightJ; // jth character of right

		int cost; // cost
		for (i = 0; i <= n; i++) {
			p[i] = i;
		}

		for (j = 1; j <= m; j++) {
			rightJ = right[j - 1];
			d[0] = j;

			for (i = 1; i <= n; i++) {
				cost = ClassUtils.equal(left[i - 1], rightJ) ? 0 : 1;
				// minimum of cell to the left+1, to the top+1, diagonally left
				// and up +cost
				d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
				// filling the matrix
				matrix[j][i] = d[i];
			}

			// copy current distance counts to 'previous row' distance counts
			tempD = p;
			p = d;
			d = tempD;
		}
		return findDetailedResults(left, right, matrix, swapped);
	}

	private static List<ReportProblem> findDetailedResults(final ResolvableType[] left, final ResolvableType[] right, final int[][] matrix, final boolean swapped) {

		List<ReportProblem> problems = new LinkedList<>();
		
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

			// case in which the character at left and right are the same,
			// in this case none of the counters will be incremented.
			if (columnIndex > 0 && rowIndex > 0 && ClassUtils.equal(left[columnIndex - 1], right[rowIndex - 1])) {
				columnIndex--;
				rowIndex--;
				continue;
			}

			String leftName = columnIndex > 0 ? ClassUtils.getName(left[columnIndex - 1]) : null;
			String rightName = rowIndex > 0 ? ClassUtils.getName(right[rowIndex - 1]) : null;

			// handling insert and delete cases.
			deleted = false;
			added = false;
			if (data - 1 == dataAtLeft && (data <= dataAtDiagonal && data <= dataAtTop) || (dataAtDiagonal == -1 && dataAtTop == -1)) { // NOPMD
				if (swapped) {
					problems.add(new ReportProblem(1, String.format(ADDED, leftName, columnIndex - 1), Type.ERROR));
					added = true;
				} else {
					problems.add(new ReportProblem(1, String.format(DELETED, rightName, rowIndex - 1), Type.ERROR));
					deleted = true;
				}
				columnIndex--;
			} else if (data - 1 == dataAtTop && (data <= dataAtDiagonal && data <= dataAtLeft) || (dataAtDiagonal == -1 && dataAtLeft == -1)) { // NOPMD
				if (swapped) {
					problems.add(new ReportProblem(1, String.format(DELETED, leftName, columnIndex - 1), Type.ERROR));
					deleted = true;
				} else {
					problems.add(new ReportProblem(1, String.format(ADDED, rightName, rowIndex - 1), Type.ERROR));
					added = true;
				}
				rowIndex--;
			}

			// substituted case
			if (!added && !deleted) {
				if (swapped) {
					problems.add(new ReportProblem(1, String.format(SUBSTITUTE, columnIndex - 1, rightName, leftName), Type.ERROR));
				} else {
					problems.add(new ReportProblem(1, String.format(SUBSTITUTE, columnIndex - 1, leftName, rightName), Type.ERROR));
				}
				columnIndex--;
				rowIndex--;
			}
		}
		return problems;
	}
}
