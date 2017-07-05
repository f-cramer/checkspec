package checkspec.util;

import static checkspec.util.MethodUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;

import checkspec.api.Visibility;
import checkspec.spring.ResolvableType;

public class MethodUtilsTest {

	private static final Method METHOD;

	static {
		try {
			METHOD = MethodUtils.class.getDeclaredMethod("toString", Method.class);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void toStringTest() {
		String result = MethodUtils.toString(METHOD);
		assertThat(result, is("public static java.lang.String toString(java.lang.reflect.Method)"));

	}

	@Test(expected = NullPointerException.class)
	public void toStringNullTest() {
		MethodUtils.toString(null);
	}

	@Test
	public void getVisibilityTest() {
		Visibility result = getVisibility(METHOD);
		assertThat(result, is(Visibility.PUBLIC));

	}

	@Test(expected = NullPointerException.class)
	public void getVisibilityNullTest() {
		getVisibility(null);
	}

	@Test
	public void getReturnTypeNameTest() {
		String result = getReturnTypeName(METHOD);
		assertThat(result, is("java.lang.String"));
	}

	@Test(expected = NullPointerException.class)
	public void getReturnTypeNameNullTest() {
		getReturnTypeName(null);
	}

	@Test
	public void getParameterListTest() {
		String result = getParameterList(METHOD);
		assertThat(result, is("java.lang.reflect.Method"));
	}

	@Test(expected = NullPointerException.class)
	public void getParameterListNullTest() {
		getParameterList(null);
	}

	@Test
	public void isAbstractTest() {
		boolean result = isAbstract(METHOD);
		assertThat(result, is(false));
	}

	@Test(expected = NullPointerException.class)
	public void isAbstractNullTest() {
		isAbstract(null);
	}

	@Test
	public void calculateParameterDistanceTest() {
		ResolvableType[] emptyParameterList = new ResolvableType[0];
		ResolvableType[] oneParameterList = { ResolvableType.forClass(Integer.TYPE) };
		ResolvableType[] anotherOneParameterList = { ResolvableType.forClass(Integer.class) };
		ResolvableType[] yetAnotherOneParameterList = { ResolvableType.forClass(String.class) };
		ResolvableType[] twoParameterList = { ResolvableType.forClass(Integer.class), ResolvableType.forClass(String.class) };

		int result = calculateParameterDistance(emptyParameterList, oneParameterList);
		assertThat(result, is(20));

		result = calculateParameterDistance(oneParameterList, emptyParameterList);
		assertThat(result, is(20));

		result = calculateParameterDistance(oneParameterList, oneParameterList);
		assertThat(result, is(0));

		result = calculateParameterDistance(oneParameterList, anotherOneParameterList);
		assertThat(result, is(5));

		result = calculateParameterDistance(oneParameterList, yetAnotherOneParameterList);
		assertThat(result, is(10));

		result = calculateParameterDistance(oneParameterList, twoParameterList);
		assertThat(result, is(25));
	}

	@Test(expected = NullPointerException.class)
	public void calculateParameterDistanceNullTest() {
		calculateParameterDistance(null, new ResolvableType[0]);
	}

	@Test(expected = NullPointerException.class)
	public void calculateParameterDistanceNullTest2() {
		calculateParameterDistance(new ResolvableType[0], null);
	}

	@Test(expected = NullPointerException.class)
	public void calculateParameterDistanceNullTest3() {
		calculateParameterDistance(null, null);
	}
}
