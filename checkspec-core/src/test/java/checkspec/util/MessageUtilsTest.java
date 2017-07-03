package checkspec.util;

import static checkspec.util.MessageUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class MessageUtilsTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void missingTest() {
		String result = missing("result");
		assertThat(result, is("result - missing"));

		exception.expect(NullPointerException.class);
		missing(null);
	}

	@Test
	public void bestFittingTest() {
		String result = bestFitting("actual", "expected");
		assertThat(result, is("actual - best fitting for \"expected\""));
	}

	@Test
	public void bestFittingNullTest() {
		exception.expect(NullPointerException.class);
		bestFitting(null, "");
	}

	@Test
	public void bestFittingNullTest2() {
		exception.expect(NullPointerException.class);
		bestFitting("", null);
	}

	@Test
	public void bestFittingNullTest3() {
		exception.expect(NullPointerException.class);
		bestFitting(null, null);
	}
}
