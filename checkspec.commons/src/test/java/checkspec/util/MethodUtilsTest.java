package checkspec.util;

import static checkspec.util.MethodUtils.*;
import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Method;

import org.junit.Test;

import checkspec.api.Visibility;
import checkspec.type.ResolvableType;

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
		assertThat(result).isEqualTo("public static java.lang.String toString(java.lang.reflect.Method)");

	}

	@Test(expected = NullPointerException.class)
	public void toStringNullTest() {
		MethodUtils.toString(null);
	}

	@Test
	public void getVisibilityTest() {
		Visibility result = getVisibility(METHOD);
		assertThat(result).isSameAs(Visibility.PUBLIC);

	}

	@Test(expected = NullPointerException.class)
	public void getVisibilityNullTest() {
		getVisibility(null);
	}

	@Test
	public void getReturnTypeNameTest() {
		String result = getReturnTypeName(METHOD);
		assertThat(result).isEqualTo("java.lang.String");
	}

	@Test(expected = NullPointerException.class)
	public void getReturnTypeNameNullTest() {
		getReturnTypeName(null);
	}

	@Test
	public void getParameterListTest() {
		String result = getParameterList(METHOD);
		assertThat(result).isEqualTo("java.lang.reflect.Method");
	}

	@Test(expected = NullPointerException.class)
	public void getParameterListNullTest() {
		getParameterList(null);
	}

	@Test
	public void isAbstractTest() {
		boolean result = isAbstract(METHOD);
		assertThat(result).isFalse();
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

		int result = calculateParameterDistance(emptyParameterList, oneParameterList, null);
		assertThat(result).isEqualTo(20);

		result = calculateParameterDistance(oneParameterList, emptyParameterList, null);
		assertThat(result).isEqualTo(20);

		result = calculateParameterDistance(oneParameterList, oneParameterList, null);
		assertThat(result).isEqualTo(0);

		result = calculateParameterDistance(oneParameterList, anotherOneParameterList, null);
		assertThat(result).isEqualTo(5);

		result = calculateParameterDistance(oneParameterList, yetAnotherOneParameterList, null);
		assertThat(result).isEqualTo(10);

		result = calculateParameterDistance(oneParameterList, twoParameterList, null);
		assertThat(result).isEqualTo(25);
	}

	@Test(expected = NullPointerException.class)
	public void calculateParameterDistanceNullTest() {
		calculateParameterDistance(null, new ResolvableType[0], null);
	}

	@Test(expected = NullPointerException.class)
	public void calculateParameterDistanceNullTest2() {
		calculateParameterDistance(new ResolvableType[0], null, null);
	}

	@Test(expected = NullPointerException.class)
	public void calculateParameterDistanceNullTest3() {
		calculateParameterDistance(null, null, null);
	}
}
