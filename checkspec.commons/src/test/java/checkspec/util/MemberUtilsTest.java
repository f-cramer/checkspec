package checkspec.util;

import static checkspec.util.MemberUtils.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import checkspec.api.Visibility;

public class MemberUtilsTest {

	private static final int PUBLIC = 1;
	private static final int PRIVATE = 2;
	private static final int PROTECTED = 4;
	private static final int PACKAGE = 0;

	@Test
	public void getVisibilityTest() {
		Visibility result = getVisibility(PUBLIC);
		assertThat(result).isSameAs(Visibility.PUBLIC);

		result = getVisibility(PRIVATE);
		assertThat(result).isSameAs(Visibility.PRIVATE);

		result = getVisibility(PROTECTED);
		assertThat(result).isSameAs(Visibility.PROTECTED);

		result = getVisibility(PACKAGE);
		assertThat(result).isSameAs(Visibility.PACKAGE);
	}
}
