package checkspec.spec;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import checkspec.extension.Extension;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

class AbstractExtendable implements Extendable {

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

	protected <RawElement, ExtensionPoint extends Extendable> void performExtensions(Extension<RawElement, ExtensionPoint>[] extensions, RawElement element, ExtensionPoint extensionPoint) {
		for (Extension<RawElement, ExtensionPoint> extension : extensions) {
			try {
				extension.extend(element, extensionPoint);
			} catch (Exception expected) {
			}
		}
	}
}
