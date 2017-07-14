package checkspec.util;

import static checkspec.util.ClassUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import checkspec.api.Visibility;
import checkspec.type.ResolvableType;
import lombok.Value;

public class ClassUtilsTest {

	private static final ClassLoader SYSTEM_CLASS_LOADER = AccessController.doPrivileged((PrivilegedAction<ClassLoader>) (() -> ClassLoader.getSystemClassLoader()));

	private static final Class<?> CLASS = ClassUtilsTest.class;
	private static final ResolvableType TYPE = ResolvableType.forClass(CLASS);

	private static final String UNDETECTABLE_CLASS_NAME = "checkspec.util.ClassThatCannotBeFound";
	private static final String ERROR_FORMAT = "error-format %s";

	private ByteArrayOutputStream baos;
	private PrintStream errorCache;

	@Before
	public void setUp() {
		errorCache = System.err;
		baos = new ByteArrayOutputStream();
		System.setErr(new PrintStream(baos));
	}

	@After
	public void tearDown() {
		System.setErr(errorCache);
	}

	@Test
	public void toStringResolvableTypeTest() {
		String result = ClassUtils.toString(TYPE);
		assertThat(result, is("public class checkspec.util.ClassUtilsTest"));

		result = ClassUtils.toString(ResolvableType.forClass(Serializable.class));
		assertThat(result, is("public interface java.io.Serializable"));

		result = ClassUtils.toString(ResolvableType.forClass(Override.class));
		assertThat(result, is("public @interface java.lang.Override"));

		result = ClassUtils.toString(ResolvableType.forClass(TimeUnit.class));
		assertThat(result, is("public enum java.util.concurrent.TimeUnit"));
	}

	@Test(expected = NullPointerException.class)
	public void toStringResolvableTypeNullTest() {
		ClassUtils.toString((ResolvableType) null);
	}

	@Test
	public void toStringClassTest() {
		String result = ClassUtils.toString(CLASS);
		assertThat(result, is("public class checkspec.util.ClassUtilsTest"));
	}

	@Test(expected = NullPointerException.class)
	public void toStringClassNullTest() {
		ClassUtils.toString((Class<?>) null);
	}

	@Test
	public void getNameResolvableTypeTest() {
		String result = getName(TYPE);
		assertThat(result, is("checkspec.util.ClassUtilsTest"));

		result = getName(ResolvableType.forClass(Integer[].class));
		assertThat(result, is("java.lang.Integer[]"));
	}

	@Test(expected = NullPointerException.class)
	public void getNameResolvableTypeNullTest() {
		getName((ResolvableType) null);
	}

	@Test
	public void getNameClassTest() {
		String result = getName(CLASS);
		assertThat(result, is("checkspec.util.ClassUtilsTest"));
	}

	@Test(expected = NullPointerException.class)
	public void getNameClassNullTest() {
		getName((Class<?>) null);
	}

	@Test
	public void getClassTest() {
		Class<?> result = ClassUtils.getClass(CLASS.getName());
		assertThat(result, is((Object) CLASS));

		result = ClassUtils.getClass(UNDETECTABLE_CLASS_NAME);
		assertThat(result, is(nullValue(Class.class)));
	}

	@Test(expected = NullPointerException.class)
	public void getClassNullTest() {
		ClassUtils.getClass(null);
	}

	@Test
	public void getClassAsStreamTest() {
		Stream<Class<?>> result = getClassAsStream(CLASS.getName());
		List<Class<?>> resultList = result.collect(Collectors.toList());
		assertThat(resultList, hasSize(1));
		assertThat(resultList, hasItem(CLASS));

		result = getClassAsStream(UNDETECTABLE_CLASS_NAME);
		resultList = result.collect(Collectors.toList());
		assertThat(resultList, is(empty()));
	}

	@Test(expected = NullPointerException.class)
	public void getClassAsStreamNullTest() {
		getClassAsStream(null);
	}

	@Test
	public void getPackageResolvableTypeTest() {
		String result = getPackage(TYPE);
		assertThat(result, is("checkspec.util"));
	}

	@Test(expected = NullPointerException.class)
	public void getPackageResolvableTypeNullTest() {
		getPackage((ResolvableType) null);
	}

	@Test
	public void getPackageClassTest() {
		String result = getPackage(CLASS);
		assertThat(result, is("checkspec.util"));
	}

	@Test(expected = NullPointerException.class)
	public void getPackageClassNullTest() {
		getPackage((Class<?>) null);
	}

	@Test
	public void getPackageStringTest() {
		String result = getPackage(CLASS.getName());
		assertThat(result, is("checkspec.util"));
	}

	@Test(expected = NullPointerException.class)
	public void getPackageStringNullTest() {
		getPackage((String) null);
	}

	@Test
	public void classSupplierTest() {
		Function<String, Class<?>> supplier = classSupplier(SYSTEM_CLASS_LOADER);
		Class<?> result = supplier.apply(CLASS.getName());
		assertThat(result, is((Object) CLASS));

		result = supplier.apply(UNDETECTABLE_CLASS_NAME);
		assertThat(result, is(nullValue(Class.class)));
	}

	@Test(expected = NullPointerException.class)
	public void classSupplierNullTest() {
		classSupplier(null);
	}

	@Test
	public void classStreamSupplierTest() {
		Function<String, Stream<Class<?>>> supplier = classStreamSupplier(SYSTEM_CLASS_LOADER);
		List<Class<?>> result = supplier.apply(CLASS.getName()).collect(Collectors.toList());
		assertThat(result, hasSize(1));
		assertThat(result, hasItem(CLASS));

		result = supplier.apply(UNDETECTABLE_CLASS_NAME).collect(Collectors.toList());
		assertThat(result, is(empty()));
	}

	@Test(expected = NullPointerException.class)
	public void classStreamSupplierNullTest() {
		classStreamSupplier(null);
	}

	@Test
	public void systemClassStreamSupplierTest() {
		Function<String, Stream<Class<?>>> supplier = systemClassStreamSupplier();
		List<Class<?>> result = supplier.apply(CLASS.getName()).collect(Collectors.toList());
		assertThat(result, hasSize(1));
		assertThat(result, hasItem(CLASS));

		result = supplier.apply(UNDETECTABLE_CLASS_NAME).collect(Collectors.toList());
		assertThat(result, is(empty()));
	}

	@Test
	public void instantiateTest() {
		Function<Class<?>, Stream<?>> supplier = instantiate();
		List<?> result = supplier.apply(CLASS).collect(Collectors.toList());
		assertThat(result, hasSize(1));
		assertThat(result.get(0), is(instanceOf(CLASS)));

		result = supplier.apply(TestClassWithoutDefaultConstructor.class).collect(Collectors.toList());
		assertThat(result, is(empty()));
	}

	@Test
	public void instantiateStringTest() {
		Function<Class<?>, Stream<?>> supplier = instantiate(ERROR_FORMAT);
		List<?> result = supplier.apply(CLASS).collect(Collectors.toList());
		assertThat(result, hasSize(1));
		assertThat(result.get(0), is(instanceOf(CLASS)));

		String errorOut = new String(baos.toByteArray(), StandardCharsets.UTF_8);
		assertThat(errorOut, isEmptyString());

		result = supplier.apply(TestClassWithoutDefaultConstructor.class).collect(Collectors.toList());
		assertThat(result, is(empty()));

		errorOut = new String(baos.toByteArray(), StandardCharsets.UTF_8);
		assertThat(errorOut, not(isEmptyString()));
	}

	@Test
	public void getVisibilityTest() {
		Visibility result = getVisibility(ResolvableType.forClass(ClassUtilsTest.class));
		assertThat(result, is(Visibility.PUBLIC));
	}

	@Test(expected = NullPointerException.class)
	public void getVisibilityNullTest() {
		getVisibility(null);
	}

	@Test
	public void isAssignableTest() {
		ResolvableType intType = ResolvableType.forClass(Integer.TYPE);
		boolean result = isAssignable(null, intType);
		assertThat(result, is(false));

		ResolvableType integerType = ResolvableType.forClass(Integer.class);
		result = isAssignable(null, integerType);
		assertThat(result, is(true));

		result = isAssignable(intType, integerType);
		assertThat(result, is(true));

		ResolvableType charType = ResolvableType.forClass(Character.TYPE);
		result = isAssignable(charType, integerType);
		assertThat(result, is(false));
	}

	@Test(expected = NullPointerException.class)
	public void isAssignableNullTest() {
		isAssignable(TYPE, null);
	}

	@Test
	public void isSuperClassTest() {
		boolean result = isSuperType(String.class, Object.class);
		assertThat(result, is(true));
	}

	@Test(expected = NullPointerException.class)
	public void isSuperClassNullTest() {
		isSuperType(null, Object.class);
	}

	@Test(expected = NullPointerException.class)
	public void isSuperClassNullTest2() {
		isSuperType(String.class, null);
	}

	@Test(expected = NullPointerException.class)
	public void isSuperClassNullTest3() {
		isSuperType(null, null);
	}

	@Test
	public void getSystemClassLoaderTest() {
		ClassLoader result = getBaseClassLoader();
		assertThat(result, is(SYSTEM_CLASS_LOADER));
	}

	@Test
	public void equalResolvableTypeTest() {
		ResolvableType firstType = ResolvableType.forClass(Integer.TYPE);
		ResolvableType secondType = ResolvableType.forClass(Integer.class);

		boolean result = equal(firstType, firstType);
		assertThat(result, is(true));

		result = equal(firstType, secondType);
		assertThat(result, is(false));
	}

	public void equalResolvableTypeNullTest() {
		boolean result = equal(null, ResolvableType.forClass(Integer.TYPE));
		assertThat(result, is(false));
		
		result = equal(ResolvableType.forClass(Integer.TYPE), null);
		assertThat(result, is(false));

		equal((ResolvableType) null, null);
		assertThat(result, is(false));
	}

	@Test
	public void equalClassTest() {
		Class<Integer> firstType = Integer.TYPE;
		Class<Integer> secondType = Integer.class;

		boolean result = equal(firstType, firstType);
		assertThat(result, is(true));

		result = equal(firstType, secondType);
		assertThat(result, is(false));
	}

	public void equalClassNullTest() {
		boolean result = equal(null, Integer.TYPE);
		assertThat(result, is(false));

		result = equal(Integer.TYPE, null);
		assertThat(result, is(false));

		result = equal((Class<?>) null, null);
		assertThat(result, is(true));
	}

	@Value
	private static class TestClassWithoutDefaultConstructor {
		private final String name;
	}
}
