package checkspec.type;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

abstract class MatchableTypeCache {

	private static final Map<Type, MatchableType> CACHE = new HashMap<>();

	public static final void put(Type type, MatchableType resolvableType) {
		CACHE.put(type, resolvableType);
	}

	public static final Optional<MatchableType> get(Type type) {
		return Optional.ofNullable(CACHE.get(type));
	}
}
