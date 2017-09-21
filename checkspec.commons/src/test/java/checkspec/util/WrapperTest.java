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

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

public class WrapperTest {

	private Wrapper<String, Exception> valueWrapper;
	private Wrapper<String, Exception> exceptionWrapper;

	@Before
	public void setUp() {
		valueWrapper = Wrapper.ofValue("");
		exceptionWrapper = Wrapper.ofThrowable(new NullPointerException());
	}

	@Test
	public void hasTrowableTest() {
		assertThat(valueWrapper.hasThrowable()).isFalse();
		assertThat(exceptionWrapper.hasThrowable()).isTrue();
	}

	@Test
	public void hasValueTest() {
		assertThat(valueWrapper.hasValue()).isTrue();
		assertThat(exceptionWrapper.hasValue()).isFalse();
	}

	@Test
	public void getValueTest() {
		assertThat(valueWrapper.getValue()).isEmpty();
		assertThat(exceptionWrapper.getValue()).isNull();
	}

	@Test
	public void getValueAsStreamTest() {
		List<String> result = valueWrapper.getValueAsStream().collect(Collectors.toList());
		assertThat(result).hasSize(1).hasOnlyOneElementSatisfying(s -> assertThat(s).isEmpty());

		result = exceptionWrapper.getValueAsStream().collect(Collectors.toList());
		assertThat(result).isEmpty();
	}

	@Test
	public void getThrowableTest() {
		assertThat(valueWrapper.getThrowable()).isNull();
		assertThat(exceptionWrapper.getThrowable()).isInstanceOf(NullPointerException.class);
	}

	@Test(expected = NullPointerException.class)
	public void ofExceptionNullTest() {
		Wrapper.ofThrowable(null);
	}

	@Test
	@SuppressWarnings("unlikely-arg-type")
	public void equalsTest() {
		boolean result = valueWrapper.equals(valueWrapper);
		assertThat(result).isTrue();
		;

		result = valueWrapper.equals(null);
		assertThat(result).isFalse();

		result = valueWrapper.equals("");
		assertThat(result).isFalse();

		Wrapper<String, Exception> otherValueWrapper = Wrapper.ofValue("nonNullString");
		result = valueWrapper.equals(otherValueWrapper);
		assertThat(result).isFalse();

		Wrapper<String, Exception> otherExceptionWrapper = Wrapper.ofThrowable(new RuntimeException());
		result = exceptionWrapper.equals(otherExceptionWrapper);
		assertThat(result).isFalse();
	}

	@Test
	public void hashCodeTest() {
		int result = valueWrapper.hashCode();
		assertThat(result).isEqualTo(3524);
	}

	@Test
	public void toStringTest() {
		String result = valueWrapper.toString();
		assertThat(result).isEqualTo("Wrapper(value=, throwable=null)");
	}
}
