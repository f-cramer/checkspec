package checkspec.util;

import static checkspec.util.FieldUtils.*;
import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Field;

import org.junit.Test;

import checkspec.api.Visibility;
import checkspec.type.MatchableType;

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
		String result = FieldUtils.createString(FIELD);
		assertThat(result).isEqualTo("private static final java.lang.reflect.Field FIELD");
	}

	@Test(expected = NullPointerException.class)
	public void createStringNullTest() {
		FieldUtils.createString(null);
	}

	@Test
	public void getTypeTest() {
		MatchableType result = getType(FIELD);
		assertThat(result).isEqualTo(MatchableType.forFieldType(FIELD));
	}

	@Test(expected = NullPointerException.class)
	public void getTypeNullTest() {
		getType(null);
	}

	@Test
	public void getTypeNameTest() {
		String result = getTypeName(FIELD);
		assertThat(result).isEqualTo("java.lang.reflect.Field");
	}

	@Test(expected = NullPointerException.class)
	public void getTypeNameNullTest() {
		getTypeName(null);
	}

	@Test
	public void getVisibilityTest() {
		Visibility result = getVisibility(FIELD);
		assertThat(result).isSameAs(Visibility.PRIVATE);
	}

	@Test(expected = NullPointerException.class)
	public void getVisibilityNullTest() {
		getVisibility(null);
	}
}
