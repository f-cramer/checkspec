package checkspec.util;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.base.Objects;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class StreamUtils {

	public static <T> Predicate<T> equalsPredicate(@NonNull T t) {
		return o -> Objects.equal(t, o);
	}

	public static <T, U> Predicate<U> equalsPredicate(@NonNull T t, @NonNull Function<U, T> converter) {
		return o -> Objects.equal(t, converter.apply(o));
	}

	public static <T, U> Predicate<T> isNotNullPredicate(@NonNull Function<T, U> converter) {
		return t -> converter.apply(t) != null;
	}

	@SuppressWarnings("unchecked")
	public static <T, U extends T> Function<T, Stream<U>> filterClass(@NonNull Class<U> clazz) {
		return e -> clazz.isInstance(e) ? Stream.of((U) e) : Stream.empty();
	}
}
