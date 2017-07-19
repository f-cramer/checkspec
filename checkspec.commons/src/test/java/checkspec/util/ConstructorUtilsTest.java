package checkspec.util;

import static checkspec.util.ConstructorUtils.*;
import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Constructor;

import org.junit.Test;

import checkspec.api.Visibility;
import checkspec.type.ResolvableType;

public class ConstructorUtilsTest {

	private static final Constructor<?> CONSTRUCTOR;
	private static final Constructor<?> STRING_CONSTRUCTOR;

	static {
		try {
			CONSTRUCTOR = ConstructorUtilsTest.class.getDeclaredConstructor();
			STRING_CONSTRUCTOR = String.class.getConstructor(String.class);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void createStringTest() {
		String result = createString(CONSTRUCTOR);
		assertThat(result).isEqualTo("public <init>()");
	}

	@Test(expected = NullPointerException.class)
	public void createStringNullTest() {
		createString(null);
	}

	@Test
	public void getVisibilityTest() {
		Visibility result = getVisibility(CONSTRUCTOR);
		assertThat(result).isSameAs(Visibility.PUBLIC);
	}

	@Test(expected = NullPointerException.class)
	public void getVisibilityNullTest() {
		getVisibility(null);
	}

	@Test
	public void getParametersAsResolvableTypeTest() {
		ResolvableType[] result = getParametersAsResolvableType(CONSTRUCTOR);
		assertThat(result).isEmpty();
	}

	@Test(expected = NullPointerException.class)
	public void getParametersAsResolvableTypeNullTest() {
		getParametersAsResolvableType(null);
	}

	@Test
	public void getParametersAsStringTest() {
		String result = getParametersAsString(STRING_CONSTRUCTOR);
		assertThat(result).isEqualTo("java.lang.String");
	}

	@Test(expected = NullPointerException.class)
	public void getParametersAsStringNullTest() {
		getParametersAsString(null);
	}
}
