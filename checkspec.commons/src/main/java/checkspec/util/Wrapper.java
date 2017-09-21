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



import java.util.stream.Stream;

import lombok.NonNull;
import lombok.Value;

/**
 * Wraps either a value or an exception.
 *
 * @author Florian Cramer
 *
 * @param <T>
 *            the value type
 * @param <E>
 *            the exception type
 */
@Value
public final class Wrapper<T, E extends Throwable> {

	private T value;
	private E throwable;

	/**
	 * Returns whether or not this wrapper contains a throwable.
	 *
	 * @return whether or not this contains a throwable
	 */
	public boolean hasThrowable() {
		return throwable != null;
	}

	/**
	 * Returns whether or not this wrapper contains a value.
	 *
	 * @return whether or not this contains a value
	 */
	public boolean hasValue() {
		return throwable == null;
	}

	/**
	 * Returns a stream containing the wrapped value if {@code this} contains a
	 * value or an empty stream otherwise.
	 *
	 * @return a stream containing the wrapped value or an empty stream
	 */
	public Stream<T> getValueAsStream() {
		return throwable == null ? Stream.of(value) : Stream.empty();
	}

	/**
	 * Creates a new wrapper from the given value.
	 *
	 * @param value
	 *            the value
	 * @param <T>
	 *            the value type
	 * @param <E>
	 *            the exception type
	 * @return a wrapper wrapping the given value
	 */
	public static <T, E extends Throwable> Wrapper<T, E> ofValue(T value) {
		return new Wrapper<>(value, null);
	}

	/**
	 * Creates a new wrapper from the given exception.
	 *
	 * @param exception
	 *            the exception
	 * @param <T>
	 *            the value type
	 * @param <E>
	 *            the exception type
	 * @return a wrapper wrapping the given exception
	 */
	public static <T, E extends Throwable> Wrapper<T, E> ofThrowable(@NonNull E exception) {
		return new Wrapper<>(null, exception);
	}
}
