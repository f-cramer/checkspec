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



import lombok.experimental.UtilityClass;

/**
 * Miscellaneous methods that are working with calculations. Mainly for internal
 * use within the framework itself.
 *
 * @author Florian Cramer
 * @see Math
 */
@UtilityClass
public final class MathUtils {

	/**
	 * Returns the product of {@code x} and {@code y}. If the calculation would
	 * leed to an over- or underflow {@code Integer#MAX_VALUE} or
	 * {@code Integer#MIN_VALUE} are returned.
	 *
	 * @param x
	 *            the first factor
	 * @param y
	 *            the second factory
	 * @return the product of x and y or {@code Integer#MAX_VALUE} or
	 *         {@code Integer#MIN_VALUE} if and over- or underflow occurres.
	 */
	public static int multiplyWithoutOverflow(int x, int y) {
		try {
			return Math.multiplyExact(x, y);
		} catch (ArithmeticException e) {
			return Math.signum(x) == Math.signum(y) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
		}
	}
}
