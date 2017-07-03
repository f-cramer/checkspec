package checkspec.util;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class WrapperTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private Wrapper<String, Exception> valueWrapper;
	private Wrapper<String, Exception> exceptionWrapper;

	@Before
	public void setUp() {
		valueWrapper = Wrapper.ofValue("");
		exceptionWrapper = Wrapper.ofException(new NullPointerException());
	}

	@Test
	public void hasTrowableTest() {
		assertThat(valueWrapper.hasThrowable(), is(false));
		assertThat(exceptionWrapper.hasThrowable(), is(true));
	}

	@Test
	public void hasValueTest() {
		assertThat(valueWrapper.hasValue(), is(true));
		assertThat(exceptionWrapper.hasValue(), is(false));
	}

	@Test
	public void getValueTest() {
		assertThat(valueWrapper.getValue(), is(""));
		assertThat(exceptionWrapper.getValue(), is(nullValue()));
	}

	@Test
	public void getValueAsStreamTest() {
		List<String> result = valueWrapper.getValueAsStream().collect(Collectors.toList());
		assertThat(result, hasSize(1));
		assertThat(result, hasItem(""));

		result = exceptionWrapper.getValueAsStream().collect(Collectors.toList());
		assertThat(result, is(empty()));
	}

	@Test
	public void getThrowableTest() {
		assertThat(valueWrapper.getThrowable(), is(nullValue()));
		assertThat(exceptionWrapper.getThrowable(), is(instanceOf(NullPointerException.class)));
	}

	@Test
	public void ofExceptionNullTest() {
		exception.expect(NullPointerException.class);
		Wrapper.ofException(null);
	}

	@Test
	@SuppressWarnings("unlikely-arg-type")
	public void equalsTest() {
		boolean result = valueWrapper.equals(valueWrapper);
		assertThat(result, is(true));

		result = valueWrapper.equals(null);
		assertThat(result, is(false));

		result = valueWrapper.equals("");
		assertThat(result, is(false));

		Wrapper<String, Exception> otherValueWrapper = Wrapper.ofValue("nonNullString");
		result = valueWrapper.equals(otherValueWrapper);
		assertThat(result, is(false));

		Wrapper<String, Exception> otherExceptionWrapper = Wrapper.ofException(new RuntimeException());
		result = exceptionWrapper.equals(otherExceptionWrapper);
		assertThat(result, is(false));
	}

	@Test
	public void hashCodeTest() {
		int result = valueWrapper.hashCode();
		assertThat(result, is(3524));
	}

	@Test
	public void toStringTest() {
		String result = valueWrapper.toString();
		assertThat(result, is("Wrapper(value=, throwable=null)"));
	}
}
