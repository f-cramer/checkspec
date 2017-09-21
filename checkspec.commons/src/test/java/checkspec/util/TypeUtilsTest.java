package checkspec.util;

import static checkspec.util.TypeUtils.*;
import static org.assertj.core.api.Assertions.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class TypeUtilsTest {

	@Test
	public void getLowestCommonSuperTypeTest() {
		List<Class<?>> classes = Arrays.asList(String.class, Integer.class);
		Class<?> result = getMostSpecificCommonSuperType(classes);
		assertThat(result).isSameAs(Serializable.class);

		classes = Arrays.asList(Serializable.class, String.class);
		result = getMostSpecificCommonSuperType(classes);
		assertThat(result).isSameAs(Serializable.class);

		classes = Arrays.asList(String.class, String.class);
		result = getMostSpecificCommonSuperType(classes);
		assertThat(result).isSameAs(String.class);

		classes = Arrays.asList(Integer.class, Long.class);
		result = getMostSpecificCommonSuperType(classes);
		assertThat(result).isSameAs(Number.class);

		classes = Arrays.asList(int.class, Integer.class);
		result = getMostSpecificCommonSuperType(classes);
		assertThat(result).isNull();

		classes = Collections.emptyList();
		result = getMostSpecificCommonSuperType(classes);
		assertThat(result).isNull();
	}

	@Test(expected = NullPointerException.class)
	public void getLowestCommonSuperTypeNullTest() {
		getMostSpecificCommonSuperType(null);
	}
}
