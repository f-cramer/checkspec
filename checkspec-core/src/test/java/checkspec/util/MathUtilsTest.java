package checkspec.util;

import static checkspec.util.MathUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class MathUtilsTest {

	@Test
	public void multiplyWithoutOverflowTest() {
		int result = multiplyWithoutOverflow(1, 2);
		assertThat(result, is(2));

		result = multiplyWithoutOverflow(Integer.MAX_VALUE, 2);
		assertThat(result, is(Integer.MAX_VALUE));

		result = multiplyWithoutOverflow(Integer.MIN_VALUE, 2);
		assertThat(result, is(Integer.MIN_VALUE));
	}
}
