package checkspec.util;

import java.util.stream.Stream;

import lombok.NonNull;
import lombok.Value;

@Value
public final class Wrapper<T, E extends Throwable> {
	private T value;
	private E throwable;

	public boolean hasThrowable() {
		return throwable != null;
	}

	public boolean hasValue() {
		return throwable == null;
	}

	public Stream<T> getValueAsStream() {
		return throwable == null ? Stream.of(value) : Stream.empty();
	}

	public static <T, E extends Throwable> Wrapper<T, E> ofValue(T value) {
		return new Wrapper<>(value, null);
	}

	public static <T, E extends Throwable> Wrapper<T, E> ofException(@NonNull E exception) {
		return new Wrapper<>(null, exception);
	}
}
