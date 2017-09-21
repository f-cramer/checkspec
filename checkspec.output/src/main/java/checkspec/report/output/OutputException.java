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

/**
 * Represents an exception that was thrown while performing an output operation
 * on a {@link checkspec.report.SpecReport}.
 *
 * @author Florian Cramer
 *
 */
public class OutputException extends Exception {

	private static final long serialVersionUID = 3346155355030336320L;

	/**
	 * Creates a new {@link OutputException} without any message or cause.
	 */
	public OutputException() {
	}

	/**
	 * Creates a new {@link OutputException} with the given {@code message} and
	 * {@code cause}.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public OutputException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates a new {@link OutputException} with the given {@code message}.
	 *
	 * @param message
	 *            the message
	 */
	public OutputException(String message) {
		super(message);
	}

	/**
	 * Creates a new {@link OutputException} with the given {@code cause}.
	 *
	 * @param cause
	 *            the cause
	 */
	public OutputException(Throwable cause) {
		super(cause);
	}
}
