package checkspec.util;

/*-
 * #%L
 * CheckSpec Commons
 * %%
 * Copyright (C) 2017 Florian Cramer
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



import static checkspec.util.ClassUtils.*;
import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import checkspec.api.Visibility;
import checkspec.type.MatchableType;
import lombok.Value;

public class ClassUtilsTest {

	private static final ClassLoader SYSTEM_CLASS_LOADER = SecurityUtils.doPrivileged(ClassLoader::getSystemClassLoader);

	private static final Class<?> CLASS = ClassUtilsTest.class;
	private static final MatchableType TYPE = MatchableType.forClass(CLASS);

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
		String result = ClassUtils.createString(TYPE);
		assertThat(result).isEqualTo("public class checkspec.util.ClassUtilsTest");

		result = ClassUtils.createString(MatchableType.forClass(Serializable.class));
		assertThat(result).isEqualTo("public interface java.io.Serializable");

		result = ClassUtils.createString(MatchableType.forClass(Override.class));
		assertThat(result).isEqualTo("public @interface java.lang.Override");

		result = ClassUtils.createString(MatchableType.forClass(TimeUnit.class));
		assertThat(result).isEqualTo("public enum java.util.concurrent.TimeUnit");
	}

	@Test(expected = NullPointerException.class)
	public void toStringResolvableTypeNullTest() {
		ClassUtils.createString((MatchableType) null);
	}

	@Test
	public void toStringClassTest() {
		String result = ClassUtils.toString(CLASS);
		assertThat(result).isEqualTo("public class checkspec.util.ClassUtilsTest");
	}

	@Test(expected = NullPointerException.class)
	public void toStringClassNullTest() {
		ClassUtils.toString((Class<?>) null);
	}

	@Test
	public void getNameResolvableTypeTest() {
		String result = getName(TYPE);
		assertThat(result).isEqualTo("checkspec.util.ClassUtilsTest");

		result = getName(MatchableType.forClass(Integer[].class));
		assertThat(result).isEqualTo("java.lang.Integer[]");
	}

	@Test(expected = NullPointerException.class)
	public void getNameResolvableTypeNullTest() {
		getName((MatchableType) null);
	}

	@Test
	public void getNameClassTest() {
		String result = getName(CLASS);
		assertThat(result).isEqualTo("checkspec.util.ClassUtilsTest");
	}

	@Test(expected = NullPointerException.class)
	public void getNameClassNullTest() {
		getName((Class<?>) null);
	}

	@Test
	public void getClassTest() {
		Class<?> result = ClassUtils.getClass(CLASS.getName());
		assertThat(result).isEqualTo(CLASS);

		result = ClassUtils.getClass(UNDETECTABLE_CLASS_NAME);
		assertThat(result).isNull();
	}

	@Test(expected = NullPointerException.class)
	public void getClassNullTest() {
		ClassUtils.getClass(null);
	}

	@Test
	public void getClassAsStreamTest() {
		Stream<Class<?>> result = getClassAsStream(CLASS.getName());
		List<Class<?>> resultList = result.collect(Collectors.toList());
		assertThat(resultList).hasSize(1);
		assertThat(resultList).containsExactly(CLASS);

		result = getClassAsStream(UNDETECTABLE_CLASS_NAME);
		resultList = result.collect(Collectors.toList());
		assertThat(resultList).isEmpty();
	}

	@Test(expected = NullPointerException.class)
	public void getClassAsStreamNullTest() {
		getClassAsStream(null);
	}

	@Test
	public void getPackageResolvableTypeTest() {
		String result = getPackage(TYPE);
		assertThat(result).isEqualTo("checkspec.util");
	}

	@Test(expected = NullPointerException.class)
	public void getPackageResolvableTypeNullTest() {
		getPackage((MatchableType) null);
	}

	@Test
	public void getPackageClassTest() {
		String result = getPackage(CLASS);
		assertThat(result).isEqualTo("checkspec.util");
	}

	@Test(expected = NullPointerException.class)
	public void getPackageClassNullTest() {
		getPackage((Class<?>) null);
	}

	@Test
	public void getPackageStringTest() {
		String result = getPackage(CLASS.getName());
		assertThat(result).isEqualTo("checkspec.util");
	}

	@Test(expected = NullPointerException.class)
	public void getPackageStringNullTest() {
		getPackage((String) null);
	}

	@Test
	public void classSupplierTest() {
		Function<String, Class<?>> supplier = classSupplier(SYSTEM_CLASS_LOADER);
		Class<?> result = supplier.apply(CLASS.getName());
		assertThat(result).isEqualTo(CLASS);

		result = supplier.apply(UNDETECTABLE_CLASS_NAME);
		assertThat(result).isNull();
	}

	@Test(expected = NullPointerException.class)
	public void classSupplierNullTest() {
		classSupplier(null);
	}

	@Test
	public void classStreamSupplierTest() {
		Function<String, Stream<Class<?>>> supplier = classStreamSupplier(SYSTEM_CLASS_LOADER);
		List<Class<?>> result = supplier.apply(CLASS.getName()).collect(Collectors.toList());
		assertThat(result).hasSize(1);
		assertThat(result).containsExactly(CLASS);

		result = supplier.apply(UNDETECTABLE_CLASS_NAME).collect(Collectors.toList());
		assertThat(result).isEmpty();
	}

	@Test(expected = NullPointerException.class)
	public void classStreamSupplierNullTest() {
		classStreamSupplier(null);
	}

	@Test
	public void systemClassStreamSupplierTest() {
		Function<String, Stream<Class<?>>> supplier = systemClassStreamSupplier();
		List<Class<?>> result = supplier.apply(CLASS.getName()).collect(Collectors.toList());
		assertThat(result).hasSize(1);
		assertThat(result).containsExactly(CLASS);

		result = supplier.apply(UNDETECTABLE_CLASS_NAME).collect(Collectors.toList());
		assertThat(result).isEmpty();
	}

	@Test
	public void instantiateTest() {
		Function<Class<?>, Stream<?>> supplier = instantiate();
		List<?> result = supplier.apply(CLASS).collect(Collectors.toList());
		assertThat(result).hasSize(1);
		assertThat(result).hasOnlyElementsOfType(CLASS);

		result = supplier.apply(TestClassWithoutDefaultConstructor.class).collect(Collectors.toList());
		assertThat(result).isEmpty();
	}

	@Test
	public void instantiateStringTest() {
		Function<Class<?>, Stream<?>> supplier = instantiate(ERROR_FORMAT);
		List<?> result = supplier.apply(CLASS).collect(Collectors.toList());
		assertThat(result).hasSize(1);
		assertThat(result).hasOnlyElementsOfType(CLASS);

		String errorOut = new String(baos.toByteArray(), StandardCharsets.UTF_8);
		assertThat(errorOut).isEmpty();

		result = supplier.apply(TestClassWithoutDefaultConstructor.class).collect(Collectors.toList());
		assertThat(result).isEmpty();

		errorOut = new String(baos.toByteArray(), StandardCharsets.UTF_8);
		assertThat(errorOut).isNotEmpty();
	}

	@Test
	public void getVisibilityTest() {
		Visibility result = getVisibility(MatchableType.forClass(ClassUtilsTest.class));
		assertThat(result).isEqualTo(Visibility.PUBLIC);
	}

	@Test(expected = NullPointerException.class)
	public void getVisibilityNullTest() {
		getVisibility(null);
	}

	@Test
	public void isSuperTypeTest() {
		boolean result = isSuperType(String.class, Object.class);
		assertThat(result).isEqualTo(true);
	}

	@Test(expected = NullPointerException.class)
	public void isSuperTypeNullTest() {
		isSuperType(null, Object.class);
	}

	@Test(expected = NullPointerException.class)
	public void isSuperTypeNullTest2() {
		isSuperType(String.class, null);
	}

	@Test(expected = NullPointerException.class)
	public void isSuperTypeNullTest3() {
		isSuperType(null, null);
	}

	@Test
	public void getBaseClassLoaderTest() {
		ClassLoader result = getBaseClassLoader();
		assertThat(result).isEqualTo(SYSTEM_CLASS_LOADER);

		URLClassLoader cl = SecurityUtils.doPrivileged(() -> new URLClassLoader(new URL[0]));
		setBaseClassLoader(cl);

		result = getBaseClassLoader();
		assertThat(result).isEqualTo(cl);

		setBaseClassLoader(null);

		result = getBaseClassLoader();
		assertThat(result).isEqualTo(SYSTEM_CLASS_LOADER);
	}

	@Test
	public void equalClassTest() {
		Class<Integer> firstType = Integer.TYPE;
		Class<Integer> secondType = Integer.class;

		boolean result = equal(firstType, firstType);
		assertThat(result).isEqualTo(true);

		result = equal(firstType, secondType);
		assertThat(result).isEqualTo(false);
	}

	@Test
	public void equalClassNullTest() {
		boolean result = equal(null, Integer.TYPE);
		assertThat(result).isEqualTo(false);

		result = equal(Integer.TYPE, null);
		assertThat(result).isEqualTo(false);

		result = equal((Class<?>) null, null);
		assertThat(result).isEqualTo(true);
	}

	@Test
	public void getLocationTest() {
		Class<?> clazz = CLASS;
		URL result = getLocation(clazz);
		String name = clazz.getName().replace('.', '/') + ".class";
		assertThat(result).isEqualTo(clazz.getClassLoader().getResource(name));

		clazz = TestClassWithoutDefaultConstructor.class;
		name = clazz.getName().replace('.', '/') + ".class";
		result = getLocation(TestClassWithoutDefaultConstructor.class);
		assertThat(result).isEqualTo(clazz.getClassLoader().getResource(name));
	}

	@Test(expected = NullPointerException.class)
	public void getLocationNullTest() {
		getLocation(null);
	}

	@Value
	private static class TestClassWithoutDefaultConstructor {
		private final String name;
	}
}
