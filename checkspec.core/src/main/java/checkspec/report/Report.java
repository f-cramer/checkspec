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

@Getter
@EqualsAndHashCode
public abstract class Report<RawType, SpecificationType extends Specification<RawType>> implements Comparable<Report<?, ?>> {

	private final SpecificationType specification;
	private final RawType implementation;
	private final String title;

	private final List<ReportProblem> problems = new ArrayList<>();

	protected Report(SpecificationType spec) {
		this(spec, null, null);
	}

	protected Report(SpecificationType spec, RawType implementation) {
		this(spec, implementation, null);
	}

	protected Report(@NonNull SpecificationType specification, RawType implementation, String title) {
		this.specification = specification;
		this.implementation = implementation;
		this.title = title;
	}

	public List<ReportProblem> getProblems() {
		return Collections.unmodifiableList(problems);
	}

	public void removeProblem(ReportProblem problem) {
		this.problems.remove(problem);
	}

	public List<Report<?, ?>> getSubReports() {
		return Collections.emptyList();
	}

	public void removeSubReport(Report<?, ?> report) {
	}

	public void addProblem(@NonNull ReportProblem entry) {
		problems.add(entry);
	}

	public void addProblems(@NonNull Collection<ReportProblem> problems) {
		problems.parallelStream()
				.filter(Objects::nonNull)
				.forEachOrdered(this.problems::add);
	}

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

	protected boolean isNameFitting() {
		return implementation == null ? false : specification.getName().equals(getRawTypeName(implementation));
	}

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

	protected abstract String getRawTypeName(RawType rawType);
}
