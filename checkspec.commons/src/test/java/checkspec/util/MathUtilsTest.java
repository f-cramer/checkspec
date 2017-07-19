package checkspec.util;

import static checkspec.util.MathUtils.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class MathUtilsTest {

	@Test
	public void multiplyWithoutOverflowTest() {
		int result = multiplyWithoutOverflow(1, 2);
		assertThat(result).isEqualTo(2);

		result = multiplyWithoutOverflow(Integer.MAX_VALUE, 2);
		assertThat(result).isEqualTo(Integer.MAX_VALUE);

		result = multiplyWithoutOverflow(Integer.MIN_VALUE, 2);
		assertThat(result).isEqualTo(Integer.MIN_VALUE);
	}
}
