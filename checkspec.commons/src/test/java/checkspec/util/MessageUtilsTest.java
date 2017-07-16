package checkspec.util;

import static checkspec.util.MessageUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class MessageUtilsTest {

	@Test
	public void missingTest() {
		String result = missing("result");
		assertThat(result, is("result - missing"));
	}

	@Test(expected = NullPointerException.class)
	public void missingNullTest() {
		missing(null);
	}

	@Test
	public void bestFittingTest() {
		String result = bestFitting("actual", "expected");
		assertThat(result, is("actual - best fitting for \"expected\""));
	}

	@Test(expected = NullPointerException.class)
	public void bestFittingNullTest() {
		bestFitting(null, "");
	}

	@Test(expected = NullPointerException.class)
	public void bestFittingNullTest2() {
		bestFitting("", null);
	}

	@Test(expected = NullPointerException.class)
	public void bestFittingNullTest3() {
		bestFitting(null, null);
	}
}
