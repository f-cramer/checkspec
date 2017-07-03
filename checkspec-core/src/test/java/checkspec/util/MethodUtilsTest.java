package checkspec.util;

import static checkspec.util.MethodUtils.*;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import java.lang.reflect.Method;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import checkspec.api.Visibility;
import checkspec.spring.ResolvableType;

public class MethodUtilsTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

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

		exception.expect(NullPointerException.class);
		MethodUtils.toString(null);
	}

	@Test
	public void getVisibilityTest() {
		Visibility result = getVisibility(METHOD);
		assertThat(result, is(Visibility.PUBLIC));

		exception.expect(NullPointerException.class);
		getVisibility(null);
	}

	@Test
	public void getReturnTypeNameTest() {
		String result = getReturnTypeName(METHOD);
		assertThat(result, is("java.lang.String"));

		exception.expect(NullPointerException.class);
		getReturnTypeName(null);
	}

	@Test
	public void getParameterListTest() {
		String result = getParameterList(METHOD);
		assertThat(result, is("java.lang.reflect.Method"));

		exception.expect(NullPointerException.class);
		getParameterList(null);
	}

	@Test
	public void isAbstractTest() {
		boolean result = isAbstract(METHOD);
		assertThat(result, is(false));

		exception.expect(NullPointerException.class);
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

	@Test
	public void calculateParameterDistanceNullTest() {
		exception.expect(NullPointerException.class);
		calculateParameterDistance(null, new ResolvableType[0]);
	}

	@Test
	public void calculateParameterDistanceNullTest2() {
		exception.expect(NullPointerException.class);
		calculateParameterDistance(new ResolvableType[0], null);
	}

	@Test
	public void calculateParameterDistanceNullTest3() {
		exception.expect(NullPointerException.class);
		calculateParameterDistance(null, null);
	}
}
