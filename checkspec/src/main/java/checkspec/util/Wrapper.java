package checkspec.util;

import java.util.stream.Stream;

import lombok.Value;

@Value
public class Wrapper<T, E extends Throwable> {
	private T wrapped;
	private E throwable;
	
	public boolean hasThrowable() {
		return throwable != null;
	}
	
	public boolean hasWrapped() {
		return throwable == null;
	}
	
	public Stream<T> getWrappedAsStream() {
		return throwable == null ? Stream.of(wrapped) : Stream.empty();
	}
	
	public static <T, E extends Throwable> Wrapper<T, E> ofValue(T value)	{
		return new Wrapper<>(value, null);
	}
	
	public static <T, E extends Throwable> Wrapper<T, E> ofException(E exception) {
		return new Wrapper<>(null, exception);
	}
}