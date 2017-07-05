package checkspec.util;

import static checkspec.util.FieldUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.junit.Test;

import checkspec.api.Visibility;
import checkspec.spring.ResolvableType;

public class FieldUtilsTest {

	private static final Field FIELD;

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
	}

	@Test(expected = NullPointerException.class)
	public void createStringNullTest() {
		FieldUtils.toString(null);
	}

	@Test
	public void getTypeTest() {
		ResolvableType result = getType(FIELD);
		assertThat(result, is(ResolvableType.forField(FIELD)));
	}

	@Test(expected = NullPointerException.class)
	public void getTypeNullTest() {
		getType(null);
	}

	@Test
	public void getTypeNameTest() {
		String result = getTypeName(FIELD);
		assertThat(result, is("java.lang.reflect.Field"));
	}

	@Test(expected = NullPointerException.class)
	public void getTypeNameNullTest() {
		getTypeName(null);
	}

	@Test
	public void getVisibilityTest() {
		Visibility result = getVisibility(FIELD);
		assertThat(result, is(Visibility.PRIVATE));
	}

	@Test(expected = NullPointerException.class)
	public void getVisibilityNullTest() {
		getVisibility(null);
	}
}
