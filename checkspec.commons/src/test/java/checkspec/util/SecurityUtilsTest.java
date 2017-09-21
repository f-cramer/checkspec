package checkspec.util;

import static checkspec.util.SecurityUtils.*;
import static org.assertj.core.api.Assertions.*;

import java.net.MalformedURLException;

import org.junit.Test;

public class SecurityUtilsTest {

	@Test
	public void doPrivilegedTest() {
		String result = doPrivileged(() -> "");
		assertThat(result).isEmpty();
	}

	@Test(expected = NullPointerException.class)
	public void doPrivilegedNullTest() {
		doPrivileged(null);
	}

	@Test
	public void doPrivilegedWithExceptionTest() {
		String result = doPrivilegedWithException(() -> "");
		assertThat(result).isEmpty();
	}

	@Test(expected = MalformedURLException.class)
	public void doPrivilegedWithExceptionExceptionTest() throws MalformedURLException {
		doPrivilegedWithException(() -> {
			throw new MalformedURLException();
		});
	}

	@Test(expected = NullPointerException.class)
	public void doPrivilegedWithExceptionNullTest() {
		doPrivilegedWithException(null);
	}
}
