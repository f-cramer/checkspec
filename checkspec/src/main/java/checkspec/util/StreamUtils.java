package checkspec.util;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

public class StreamUtils {

	public static <T> Predicate<T> equalsPredicate(@Nonnull T t) {
		return o -> t.equals(o);
	}
	
	public static <T> Stream<T> stream(T[] array) {
		return Arrays.stream(array);
	}
	
	public static <T> Stream<T> parallelStream(T[] array) {
		return Arrays.stream(array).parallel();
	}
}
