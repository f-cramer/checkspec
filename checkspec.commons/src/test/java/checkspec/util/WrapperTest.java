package checkspec.util;

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
