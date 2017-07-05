/*
 * Copyright 2002-2017 the original author or authors.
 *
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
 */

package checkspec.spring;

/**
 * Miscellaneous {@link String} utility methods.
 *
 * <p>
 * Mainly for internal use within the framework; consider
 * <a href="http://commons.apache.org/proper/commons-lang/">Apache's Commons
 * Lang</a> for a more comprehensive suite of {@code String} utilities.
 *
 * <p>
 * This class delivers some simple functionality that should really be provided
 * by the core Java {@link String} and {@link StringBuilder} classes. It also
 * provides easy-to-use methods to convert between delimited strings, such as
 * CSV strings, and collections and arrays.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Keith Donald
 * @author Rob Harrop
 * @author Rick Evans
 * @author Arjen Poutsma
 * @author Sam Brannen
 * @author Brian Clozel
 * @since 16 April 2001
 */
abstract class StringUtils {

	// ---------------------------------------------------------------------
	// General convenience methods for working with Strings
	// ---------------------------------------------------------------------

	/**
	 * Check whether the given {@code String} is empty.
	 * <p>
	 * This method accepts any Object as an argument, comparing it to {@code null}
	 * and the empty String. As a consequence, this method will never return
	 * {@code true} for a non-null non-String object.
	 * <p>
	 * The Object signature is useful for general attribute handling code that
	 * commonly deals with Strings but generally has to iterate over Objects since
	 * attributes may e.g. be primitive value objects as well.
	 * 
	 * @param str
	 *            the candidate String
	 * @since 3.2.1
	 */
	public static boolean isEmpty(Object str) {
		return (str == null || "".equals(str));
	}

	/**
	 * Check that the given {@code CharSequence} is neither {@code null} nor of
	 * length 0.
	 * <p>
	 * Note: this method returns {@code true} for a {@code CharSequence} that purely
	 * consists of whitespace.
	 * <p>
	 * 
	 * <pre class="code">
	 * StringUtils.hasLength(null) = false
	 * StringUtils.hasLength("") = false
	 * StringUtils.hasLength(" ") = true
	 * StringUtils.hasLength("Hello") = true
	 * </pre>
	 * 
	 * @param str
	 *            the {@code CharSequence} to check (may be {@code null})
	 * @return {@code true} if the {@code CharSequence} is not {@code null} and has
	 *         length
	 * @see #hasText(String)
	 */
	public static boolean hasLength(CharSequence str) {
		return (str != null && str.length() > 0);
	}

	/**
	 * Check whether the given {@code CharSequence} contains actual <em>text</em>.
	 * <p>
	 * More specifically, this method returns {@code true} if the
	 * {@code CharSequence} is not {@code null}, its length is greater than 0, and
	 * it contains at least one non-whitespace character.
	 * <p>
	 * 
	 * <pre class="code">
	 * StringUtils.hasText(null) = false
	 * StringUtils.hasText("") = false
	 * StringUtils.hasText(" ") = false
	 * StringUtils.hasText("12345") = true
	 * StringUtils.hasText(" 12345 ") = true
	 * </pre>
	 * 
	 * @param str
	 *            the {@code CharSequence} to check (may be {@code null})
	 * @return {@code true} if the {@code CharSequence} is not {@code null}, its
	 *         length is greater than 0, and it does not contain whitespace only
	 * @see Character#isWhitespace
	 */
	public static boolean hasText(CharSequence str) {
		if (!hasLength(str)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the given {@code String} contains actual <em>text</em>.
	 * <p>
	 * More specifically, this method returns {@code true} if the {@code String} is
	 * not {@code null}, its length is greater than 0, and it contains at least one
	 * non-whitespace character.
	 * 
	 * @param str
	 *            the {@code String} to check (may be {@code null})
	 * @return {@code true} if the {@code String} is not {@code null}, its length is
	 *         greater than 0, and it does not contain whitespace only
	 * @see #hasText(CharSequence)
	 */
	public static boolean hasText(String str) {
		return hasText((CharSequence) str);
	}

	// ---------------------------------------------------------------------
	// Convenience methods for working with String arrays
	// ---------------------------------------------------------------------

	/**
	 * Convert a {@code String} array into a delimited {@code String} (e.g. CSV).
	 * <p>
	 * Useful for {@code toString()} implementations.
	 * 
	 * @param arr
	 *            the array to display
	 * @param delim
	 *            the delimiter to use (typically a ",")
	 * @return the delimited {@code String}
	 */
	public static String arrayToDelimitedString(Object[] arr, String delim) {
		if (ObjectUtils.isEmpty(arr)) {
			return "";
		}
		if (arr.length == 1) {
			return ObjectUtils.nullSafeToString(arr[0]);
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			if (i > 0) {
				sb.append(delim);
			}
			sb.append(arr[i]);
		}
		return sb.toString();
	}
}
