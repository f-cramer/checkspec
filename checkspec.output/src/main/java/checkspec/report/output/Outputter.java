package checkspec.report.output;

/*-
 * #%L
 * CheckSpec Output
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

import checkspec.report.SpecReport;

/**
 * Represents a form of outputting a single {@link SpecReport}.
 *
 * @author Florian Cramer
 *
 */
public interface Outputter {

	/**
	 * Performs some output operation on the given {@link SpecReport}.
	 *
	 * @param report
	 *            the report
	 * @throws OutputException
	 *             if an exception happened while outputting
	 */
	void output(SpecReport report) throws OutputException;

	/**
	 * Called after the last {@link SpecReport} has been output with this
	 * outputter.
	 */
	default void finished() throws OutputException {
	}
}
