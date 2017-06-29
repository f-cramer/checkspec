package checkspec.extension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

public class AbstractExtendable<ExtensionPoint extends Extendable, Payload> implements Extendable {

	@Getter(AccessLevel.NONE)
	private final Map<Class<?>, Object> extensions = new HashMap<>();

	@Override
	@SuppressWarnings("unchecked")
	public <T> Optional<T> addExtension(@NonNull T object) {
		Class<?> clazz = object.getClass();
		return (Optional<T>) Optional.ofNullable(extensions.put(clazz, object));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Optional<T> getExtension(@NonNull Class<T> clazz) {
		return (Optional<T>) Optional.ofNullable(extensions.get(clazz));
	}

	protected void performExtensions(Extension<ExtensionPoint, Payload>[] extensions, ExtensionPoint extensionPoint, Payload payload) {
		for (Extension<ExtensionPoint, Payload> extension : extensions) {
			try {
				extension.extend(extensionPoint, payload);
			} catch (Exception expected) {
			}
		}
	}
}
