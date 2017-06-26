package checkspec.util;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.base.Objects;

import lombok.NonNull;

public class StreamUtils {

	public static <T> Predicate<T> equalsPredicate(@NonNull T t) {
		return o -> t.equals(o);
	}
	
	public static <T, U> Predicate<U> equalsPredicate(@NonNull T t, @NonNull Function<U, T> converter) {
		return o -> Objects.equal(t, converter.apply(o));
	}
	
	public static <T, U> Predicate<T> isNullPredicate(Function<T, U> converter) {
		return t -> converter.apply(t) != null;
	}

	public static <T> Stream<T> stream(T[] array) {
		return Arrays.stream(array);
	}

	public static <T> Stream<T> parallelStream(T[] array) {
		return Arrays.stream(array).parallel();
	}

	@SuppressWarnings("unchecked")
	public static <T, U extends T> Function<T, Stream<U>> filterClass(Class<U> clazz) {
		return e -> clazz.isInstance(e) ? Stream.of((U) e) : Stream.empty();
	}
}
