package checkspec.type;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

abstract class ResolvableTypeCache {

	private static final Map<Type, ResolvableType> CACHE = new HashMap<>();

	public static final void put(Type type, ResolvableType resolvableType) {
		CACHE.put(type, resolvableType);
	}

	public static final Optional<ResolvableType> get(Type type) {
		return Optional.ofNullable(CACHE.get(type));
	}
}
