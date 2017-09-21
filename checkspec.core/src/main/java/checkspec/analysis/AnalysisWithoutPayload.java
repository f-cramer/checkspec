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



/**
 * A specific analysis that doesn not have a payload.
 *
 * @author Florian Cramer
 *
 * @param <RawType>
 *            the raw type
 * @param <SpecificationType>
 *            the specification type
 * @param <ReturnType>
 *            the return type
 */
public interface AnalysisWithoutPayload<RawType, SpecificationType, ReturnType> extends Analysis<RawType, SpecificationType, ReturnType, Void> {

	/**
	 * Analyzes the given raw type with the given specification.
	 *
	 * @param actual
	 *            the possible implementation
	 * @param specification
	 *            the specification
	 * @return the return value
	 */
	default ReturnType analyze(RawType actual, SpecificationType specification) {
		return analyze(actual, specification, null);
	}
}
