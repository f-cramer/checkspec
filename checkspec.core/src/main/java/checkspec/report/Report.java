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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import checkspec.specification.Specification;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * An abstract base class for all reports. A report contains a specification, an
 * optional implementation and a title as well as problems and sub reports.
 *
 * @author Florian Cramer
 *
 * @param <RawType>
 *            the implementation type
 * @param <SpecificationType>
 *            the specification type
 */
@Getter
@EqualsAndHashCode
public abstract class Report<RawType, SpecificationType extends Specification<RawType>> implements Comparable<Report<?, ?>> {

	private final SpecificationType specification;
	private final RawType implementation;
	private final String title;

	private final List<ReportProblem> problems = new ArrayList<>();

	/**
	 * Creates a new empty report from the given specification.
	 *
	 * @param specification
	 *            the specification
	 */
	protected Report(SpecificationType specification) {
		this(specification, null, null);
	}

	/**
	 * Creates a new report from the given specification and implementation.
	 *
	 * @param specification
	 *            the specification
	 * @param implementation
	 *            the implementation
	 */
	protected Report(SpecificationType specification, RawType implementation) {
		this(specification, implementation, null);
	}

	/**
	 * Creates a new report from the given specification, implementation and
	 * title.
	 *
	 * @param specification
	 *            the specification
	 * @param implementation
	 *            the implementation
	 * @param title
	 *            the title
	 */
	protected Report(@NonNull SpecificationType specification, RawType implementation, String title) {
		this.specification = specification;
		this.implementation = implementation;
		this.title = title;
	}

	/**
	 * Returns the previously added problems.
	 *
	 * @return the problems
	 */
	public List<ReportProblem> getProblems() {
		return Collections.unmodifiableList(problems);
	}

	/**
	 * Remove the given problem from this report.
	 *
	 * @param problem
	 *            the problem
	 */
	public void removeProblem(ReportProblem problem) {
		this.problems.remove(problem);
	}

	/**
	 * Returns the previously added sub reports.
	 *
	 * @return the sub reports
	 */
	public List<Report<?, ?>> getSubReports() {
		return Collections.emptyList();
	}

	/**
	 * Removes the given sub report from this report.
	 *
	 * @param report
	 *            the report
	 */
	public void removeSubReport(Report<?, ?> report) {
	}

	/**
	 * Adds the given problem to this report.
	 *
	 * @param problem
	 *            the problem
	 */
	public void addProblem(@NonNull ReportProblem problem) {
		problems.add(problem);
	}

	/**
	 * Adds the given problems to this report.
	 *
	 * @param problems
	 *            the problems
	 */
	public void addProblems(@NonNull Collection<ReportProblem> problems) {
		problems.parallelStream()
				.filter(Objects::nonNull)
				.forEachOrdered(this.problems::add);
	}

	/**
	 * Returns the type of this report. The type of a report is the worst type
	 * of all of its sub reports and problem or {@link ReportType#SUCCESS
	 * SUCCESS} if there are no sub reports and problems.
	 *
	 * @return the reports type
	 */
	public ReportType getType() {
		if (implementation == null) {
			return ReportType.ERROR;
		}

		Stream<ReportType> reportTypes = getSubReports().parallelStream().map(Report::getType);
		Stream<ReportType> problemTypes = problems.parallelStream().map(ReportProblem::getType).map(ReportProblemType::toReportType);

		return Stream.concat(reportTypes, problemTypes).parallel()
				.max(Comparator.naturalOrder())
				.orElse(ReportType.SUCCESS);
	}

	/**
	 * Returns whether or not the name or the implementation fits the name of
	 * the specification.
	 *
	 * @return whether or not the name or the implementation fits the name of
	 *         the specification.
	 */
	protected boolean isNameFitting() {
		return implementation == null ? false : specification.getName().equals(getRawTypeName(implementation));
	}

	/**
	 * Returns the score of this report. The score is the the sum of the scores
	 * of all sub reports and problems.
	 *
	 * @return the reports score
	 */
	public int getScore() {
		if (getImplementation() == null) {
			return 100;
		} else {
			int problemsSum = problems.parallelStream().mapToInt(ReportProblem::getScore).sum();
			int subReportsSum = getSubReports().parallelStream().mapToInt(Report::getScore).sum();

			return problemsSum + subReportsSum;
		}
	}

	@Override
	public String toString() {
		int score = getScore();
		return score == 0 ? String.valueOf(getTitle()) : String.format("%s [%d]", getTitle(), score);
	}

	@Override
	public int compareTo(Report<?, ?> report) {
		if (report == null) {
			return 1;
		}

		int cmp = Integer.compare(getScore(), report.getScore());
		if (cmp != 0) {
			return cmp;
		}

		return title.compareToIgnoreCase(report.getTitle());
	}

	/**
	 * Returns the type name of the given raw type.
	 *
	 * @param rawType
	 *            the raw type
	 * @return the type name of the given raw type
	 */
	protected abstract String getRawTypeName(RawType rawType);
}
