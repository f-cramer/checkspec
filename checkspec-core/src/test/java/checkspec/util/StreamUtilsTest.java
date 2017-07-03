package checkspec.util;

import static checkspec.util.StreamUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class StreamUtilsTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void equalsPredicateTest() {
		Predicate<String> result = equalsPredicate("");
		boolean appliedResult = result.test("nonEmptyString");
		assertThat(appliedResult, is(false));

		appliedResult = result.test("");
		assertThat(appliedResult, is(true));

		exception.expect(NullPointerException.class);
		equalsPredicate(null);
	}

	@Test
	public void equalsPredicateWithConverterTest() {
		Predicate<String> result = equalsPredicate("", Function.identity());
		boolean appliedResult = result.test("nonEmptyString");
		assertThat(appliedResult, is(false));

		appliedResult = result.test("");
		assertThat(appliedResult, is(true));

		exception.expect(NullPointerException.class);
		equalsPredicate(null, Function.identity());
		equalsPredicate("", null);
		equalsPredicate(null, null);
	}

	@Test
	public void equalsPredicateWitConverterNullTest() {
		exception.expect(NullPointerException.class);
		equalsPredicate(null, Function.identity());
	}

	@Test
	public void equalsPredicateWitConverterNullTest2() {
		exception.expect(NullPointerException.class);
		equalsPredicate("", null);
	}

	@Test
	public void equalsPredicateWitConverterNullTest3() {
		exception.expect(NullPointerException.class);
		equalsPredicate(null, null);
	}

	@Test
	public void isNullPredicateTest() {
		Predicate<String> result = isNotNullPredicate(Function.identity());
		boolean appliedResult = result.test("nonEmptyString");
		assertThat(appliedResult, is(true));

		appliedResult = result.test(null);
		assertThat(appliedResult, is(false));

		exception.expect(NullPointerException.class);
		isNotNullPredicate(null);
	}

	@Test
	public void filterClassTest() {
		Function<Object, Stream<String>> result = filterClass(String.class);
		List<String> appliedResult = result.apply("").collect(Collectors.toList());
		assertThat(appliedResult, hasSize(1));
		assertThat(appliedResult, hasItem(""));

		appliedResult = result.apply(new Object()).collect(Collectors.toList());
		assertThat(appliedResult, is(empty()));

		exception.expect(NullPointerException.class);
		filterClass(null);
	}
}
