package checkspec.util;

import static checkspec.util.StreamUtils.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

public class StreamUtilsTest {

	@Test
	public void equalsPredicateTest() {
		Predicate<String> result = equalsPredicate("");
		boolean appliedResult = result.test("nonEmptyString");
		assertThat(appliedResult).isFalse();

		appliedResult = result.test("");
		assertThat(appliedResult).isTrue();
	}

	@Test(expected = NullPointerException.class)
	public void equalsPredicateNullTest() {
		equalsPredicate(null);
	}

	@Test
	public void equalsPredicateWithConverterTest() {
		Predicate<String> result = equalsPredicate("", Function.identity());
		boolean appliedResult = result.test("nonEmptyString");
		assertThat(appliedResult).isFalse();

		appliedResult = result.test("");
		assertThat(appliedResult).isTrue();
	}

	@Test(expected = NullPointerException.class)
	public void equalsPredicateWithConverterNullTest() {
		equalsPredicate(null, Function.identity());
	}

	@Test(expected = NullPointerException.class)
	public void equalsPredicateWithConverterNullTest2() {
		equalsPredicate("", null);
	}

	@Test(expected = NullPointerException.class)
	public void equalsPredicateWithConverterNullTest3() {
		equalsPredicate(null, null);
	}

	@Test(expected = NullPointerException.class)
	public void equalsPredicateWitConverterNullTest() {
		equalsPredicate(null, Function.identity());
	}

	@Test(expected = NullPointerException.class)
	public void equalsPredicateWitConverterNullTest2() {
		equalsPredicate("", null);
	}

	@Test(expected = NullPointerException.class)
	public void equalsPredicateWitConverterNullTest3() {
		equalsPredicate(null, null);
	}

	@Test
	public void inPredicateTest() {
		List<Integer> collection = Arrays.asList(1, 2, 3);
		Predicate<Integer> result = inPredicate(collection, Function.identity());
		boolean appliedResult = result.test(1);
		assertThat(appliedResult).isTrue();

		appliedResult = result.test(4);
		assertThat(appliedResult).isFalse();
	}

	@Test(expected = NullPointerException.class)
	public void inPredicateNullTest() {
		inPredicate(null, Function.identity());
	}

	@Test(expected = NullPointerException.class)
	public void inPredicateNullTest2() {
		inPredicate(Arrays.asList(1), null);
	}

	@Test(expected = NullPointerException.class)
	public void inPredicateNullTest3() {
		inPredicate(null, null);
	}

	@Test
	public void isNullPredicateTest() {
		Predicate<String> result = isNotNullPredicate(Function.identity());
		boolean appliedResult = result.test("nonEmptyString");
		assertThat(appliedResult).isTrue();

		appliedResult = result.test(null);
		assertThat(appliedResult).isFalse();
	}

	@Test(expected = NullPointerException.class)
	public void isNullPredicateNullTest() {
		isNotNullPredicate(null);
	}

	@Test
	public void filterClassTest() {
		Function<Object, Stream<String>> result = filterClass(String.class);
		List<String> appliedResult = result.apply("").collect(Collectors.toList());
		assertThat(appliedResult).hasSize(1).hasOnlyOneElementSatisfying(s -> assertThat(s).isEmpty());

		appliedResult = result.apply(new Object()).collect(Collectors.toList());
		assertThat(appliedResult).isEmpty();
	}

	@Test(expected = NullPointerException.class)
	public void filterClassNullTest() {
		filterClass(null);
	}
}
