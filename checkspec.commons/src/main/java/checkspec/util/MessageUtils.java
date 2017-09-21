package checkspec.util;

/*-
 * #%L
 * CheckSpec Commons
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



import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Miscellaneous methods for creating messages. Mainly for internal use within
 * the framework itself.
 *
 * @author Florian Cramer
 */
@UtilityClass
public final class MessageUtils {

	private static final String MISSING = "%s - missing";
	private static final String BEST_FITTING = "%s - best fitting for \"%s\"";

	/**
	 * Creates a <i>missing</i> message.
	 *
	 * @param expected
	 *            the expected value that is missing
	 * @return the message
	 */
	public static String missing(@NonNull String expected) {
		return String.format(MISSING, expected);
	}

	/**
	 * Creates a <i>best fitting</i> message.
	 *
	 * @param actual
	 *            the actual value
	 * @param expected
	 *            the value as it should be
	 * @return the message
	 */
	public static String bestFitting(@NonNull String actual, @NonNull String expected) {
		return String.format(BEST_FITTING, actual, expected);
	}
}
