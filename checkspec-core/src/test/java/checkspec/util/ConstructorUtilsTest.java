package checkspec.util;

import static checkspec.util.ConstructorUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Constructor;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import checkspec.api.Visibility;
import checkspec.spring.ResolvableType;

public class ConstructorUtilsTest {

	private static final Constructor<?> CONSTRUCTOR;
	private static final Constructor<?> STRING_CONSTRUCTOR;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

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
		assertThat(result, is("public <init>()"));

		exception.expect(NullPointerException.class);
		createString(null);
	}

	@Test
	public void getVisibilityTest() {
		Visibility result = getVisibility(CONSTRUCTOR);
		assertThat(result, is(Visibility.PUBLIC));

		exception.expect(NullPointerException.class);
		getVisibility(null);
	}

	@Test
	public void getParametersAsResolvableTypeTest() {
		ResolvableType[] result = getParametersAsResolvableType(CONSTRUCTOR);
		assertThat(result, is(emptyArray()));

		exception.expect(NullPointerException.class);
		getParametersAsResolvableType(null);
	}

	@Test
	public void getParametersAsStringTest() {
		String result = getParametersAsString(STRING_CONSTRUCTOR);
		assertThat(result, is("java.lang.String"));

		exception.expect(NullPointerException.class);
		getParametersAsString(null);
	}
}
