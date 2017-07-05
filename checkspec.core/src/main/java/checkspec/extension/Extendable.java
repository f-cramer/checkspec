package checkspec.extension;

import java.util.Optional;

public interface Extendable {

	<T> Optional<T> addExtension(T object);

	<T> Optional<T> getExtension(Class<T> clazz);
}
