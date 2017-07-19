package checkspec.util;

import static checkspec.util.TypeUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class TypeUtilsTest {

	@Test
	public void getLowestCommonSuperTypeTest() {
		List<Class<?>> classes = Arrays.asList(String.class, Integer.class);
		Class<?> result = getLowestCommonSuperType(classes);
		assertThat(result, is((Object) Serializable.class));

		classes = Arrays.asList(Serializable.class, String.class);
		result = getLowestCommonSuperType(classes);
		assertThat(result, is((Object) Serializable.class));

		classes = Arrays.asList(String.class, String.class);
		result = getLowestCommonSuperType(classes);
		assertThat(result, is((Object) String.class));

		classes = Arrays.asList(Integer.class, Long.class);
		result = getLowestCommonSuperType(classes);
		assertThat(result, is((Object) Number.class));

		classes = Arrays.asList(int.class, Integer.class);
		result = getLowestCommonSuperType(classes);
		assertThat(result, is(nullValue()));

		classes = Collections.emptyList();
		result = getLowestCommonSuperType(classes);
		assertThat(result, is(nullValue()));
	}

	@Test(expected = NullPointerException.class)
	public void getLowestCommonSuperTypeNullTest() {
		getLowestCommonSuperType(null);
	}

	@Test(expected = NullPointerException.class)
	public void getLowestCommonSuperTypesNullTest() {
		getLowestCommonSuperTypes(null);
	}
}
