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

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.report.ClassReport;
import checkspec.specification.ClassSpecification;
import checkspec.type.MatchableType;

public interface ClassAnalysis<ReturnType> extends Analysis<MatchableType, ClassSpecification, ReturnType, MultiValuedMap<Class<?>, Class<?>>> {

	void add(ClassReport report, ReturnType returnType);

	default int getPriority() {
		return Integer.MIN_VALUE;
	}
}
