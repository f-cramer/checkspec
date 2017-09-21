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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.report.ReportProblem;
import checkspec.report.ReportProblemType;
import checkspec.specification.ExecutableSpecification;
import checkspec.type.MatchableType;
import checkspec.util.ClassUtils;

public class ExceptionsAnalysis implements Analysis<Executable, ExecutableSpecification<? extends Executable>, List<ReportProblem>, MultiValuedMap<Class<?>, Class<?>>> {

	private static final String SHOULD = "should throw throwable of type \"%s\"";
	private static final String SHOULD_NOT = "should not throw thowable of type \"%s\"";

	@Override
	public List<ReportProblem> analyze(Executable executable, ExecutableSpecification<? extends Executable> spec, MultiValuedMap<Class<?>, Class<?>> oldReports) {
		List<ReportProblem> problems = new ArrayList<>();

		List<MatchableType> notFoundThrowables = Arrays.stream(executable.getGenericExceptionTypes())
				.map(MatchableType::forType)
				// needs to be mutable
				.collect(Collectors.toCollection(ArrayList::new));
		MatchableType[] specifications = Arrays.stream(spec.getExceptions())
				.map(ExceptionSpecification::getRawElement)
				.toArray(MatchableType[]::new);

		for (MatchableType specification : specifications) {
			Optional<MatchableType> interf = notFoundThrowables.parallelStream()
					.filter(i -> specification.matches(i, oldReports).evaluate(true, true, false))
					.findAny();

			if (interf.isPresent()) {
				notFoundThrowables.remove(interf.get());
			} else {
				problems.add(new ReportProblem(15, String.format(SHOULD, ClassUtils.getName(specification)), ReportProblemType.ERROR));
			}
		}

		for (MatchableType notFoundInterface : notFoundThrowables) {
			problems.add(new ReportProblem(15, String.format(SHOULD_NOT, ClassUtils.getName(notFoundInterface)), ReportProblemType.ERROR));
		}

		return problems;
	}
}
