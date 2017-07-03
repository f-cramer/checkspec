package checkspec.util;

import static checkspec.util.FieldUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import checkspec.api.Visibility;
import checkspec.spring.ResolvableType;

public class FieldUtilsTest {

	private static final Field FIELD;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	static {
		try {
			FIELD = FieldUtilsTest.class.getDeclaredField("FIELD");
		} catch (NoSuchFieldException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void createStringTest() {
		String result = FieldUtils.toString(FIELD);
		assertThat(result, is("private static final java.lang.reflect.Field FIELD"));

		exception.expect(NullPointerException.class);
		FieldUtils.toString(null);
	}

	@Test
	public void getTypeTest() {
		ResolvableType result = getType(FIELD);
		assertThat(result, is(ResolvableType.forField(FIELD)));

		exception.expect(NullPointerException.class);
		getType(null);
	}

	@Test
	public void getTypeNameTest() {
		String result = getTypeName(FIELD);
		assertThat(result, is("java.lang.reflect.Field"));

		exception.expect(NullPointerException.class);
		getTypeName(null);
	}

	@Test
	public void getVisibilityTest() {
		Visibility result = getVisibility(FIELD);
		assertThat(result, is(Visibility.PRIVATE));

		exception.expect(NullPointerException.class);
		getVisibility(null);
	}
}
