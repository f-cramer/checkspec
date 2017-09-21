package checkspec.util;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Miscellaneous methods that are working with streams. Mainly for internal use
 * within the framework itself.
 *
 * @author Florian Cramer
 * @see Stream
 */
@UtilityClass
public final class StreamUtils {

	/**
	 * Creates a predicate that checks whether a given argument is {@code equal}
	 * to {@code t}.
	 *
	 * @param t
	 *            an arbitrary non-null instance
	 * @param <T>
	 *            the type parameter for the returned predicate
	 * @return a predicate checking for equality with {@code t}
	 */
	public static <T> Predicate<T> equalsPredicate(@NonNull T t) {
		return o -> Objects.equals(t, o);
	}

	/**
	 * Creates a predicate that checks whether a converted given argument is
	 * {@code equal} to {@code t}.
	 *
	 * @param t
	 *            an arbitrary non-null instance
	 * @param converter
	 *            a converter
	 * @param <T>
	 *            the type parameter for {@code t}
	 * @param <U>
	 *            the type parameter of the returned predicate
	 * @return a predicate checking for of a converted value equality with
	 *         {@code t}
	 */
	public static <T, U> Predicate<U> equalsPredicate(@NonNull T t, @NonNull Function<U, ? extends T> converter) {
		return o -> Objects.equals(t, converter.apply(o));
	}

	/**
	 * Creates a predicate that checks whether a converted given argument is in
	 * a given collection.
	 *
	 * @param t
	 *            the collection
	 * @param converter
	 *            the converter
	 * @param <T>
	 *            the collection type
	 * @param <U>
	 *            the type parameter of the returned predicate
	 * @return a predicate checking whether the converted value is in the
	 *         collection
	 */
	public static <T, U> Predicate<U> inPredicate(@NonNull Collection<T> t, @NonNull Function<U, T> converter) {
		return o -> t.contains(converter.apply(o));
	}

	/**
	 * Creates a predicate that checks whether a converted given argument is
	 * non-null.
	 *
	 * @param converter
	 *            the converter
	 * @param <T>
	 *            the type parameter
	 * @param <U>
	 *            the type parameter of the returned predicate
	 * @return a predicate checking whether or not a converted value is non-null
	 */
	public static <T, U> Predicate<T> isNotNullPredicate(@NonNull Function<T, U> converter) {
		return t -> converter.apply(t) != null;
	}

	/**
	 * Creates a function that filters instances that are not from a given
	 * class. This can be used to filter a stream for a specific class.
	 *
	 * @param clazz
	 *            the class
	 * @param <T>
	 *            the type parameter of the given stream
	 * @param <U>
	 *            the type parameter of the returned stream
	 * @return function that filters instances that are not from a given class
	 */
	@SuppressWarnings("unchecked")
	public static <T, U extends T> Function<T, Stream<U>> filterClass(@NonNull Class<U> clazz) {
		return e -> clazz.isInstance(e) ? Stream.of((U) e) : Stream.empty();
	}

	/**
	 * Creates a stream from a given enumeration.
	 *
	 * @param enumeration
	 *            the enumeration
	 * @param <T>
	 *            the type parameter of the returned stream
	 * @return a stream based on the enumeration
	 */
	public static <T> Stream<T> stream(Enumeration<T> enumeration) {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(enumeration), Spliterator.ORDERED), false);
	}

	/**
	 * Creates an iterator for a given enumeration.
	 *
	 * @param enumeration
	 *            the enumeration
	 * @param <T>
	 *            the type parameter of the returned iterator
	 * @return an iterator base on the enumeration
	 */
	public static <T> Iterator<T> iterator(Enumeration<T> enumeration) {
		return new Iterator<T>() {
			@Override
			public boolean hasNext() {
				return enumeration.hasMoreElements();
			}

			@Override
			public T next() {
				return enumeration.nextElement();
			}
		};
	}
}
