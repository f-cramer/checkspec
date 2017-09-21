package checkspec.cli;

/*-
 * #%L
 * CheckSpec CLI
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
 * Represents an exception that is thrown if an error happens in the command
 * line interface.
 *
 * @author Florian Cramer
 *
 */
public class CommandLineException extends Exception {

	private static final long serialVersionUID = -2579147946992451321L;

	/**
	 * Creates a new {@link CommandLineException} without any message or cause.
	 */
	public CommandLineException() {
	}

	/**
	 * Creates a new {@link CommandLineException} with the given {@code message}
	 * and {@code cause}.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public CommandLineException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates a new {@link CommandLineException} with the given
	 * {@code message}.
	 *
	 * @param message
	 *            the message
	 */
	public CommandLineException(String message) {
		super(message);
	}

	/**
	 * Creates a new {@link CommandLineException} with the given {@code cause}.
	 *
	 * @param cause
	 *            the cause
	 */
	public CommandLineException(Throwable cause) {
		super(cause);
	}
}
