package checkspec.type;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.experimental.UtilityClass;

@UtilityClass
class MatchableTypeCache {

	private static final Map<Type, MatchableType> CACHE;

	static {
		CACHE = new HashMap<>();
		CACHE.put(Object.class, MatchableType.OBJECT);
	}

	public static final Optional<MatchableType> put(Type type, MatchableType matchableType) {
		return Optional.ofNullable(CACHE.put(type, matchableType));
	}

	public static final Optional<MatchableType> get(Type type) {
		return Optional.ofNullable(CACHE.get(type));
	}
}
