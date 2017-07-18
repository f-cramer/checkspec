package checkspec.type;

import java.lang.reflect.Type;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@EqualsAndHashCode
abstract class AbstractResolvableType<RawType extends Type> implements ResolvableType {

	protected RawType rawType;

	protected AbstractResolvableType(@NonNull final RawType rawType) {
		this.rawType = rawType;
		ResolvableTypeCache.put(rawType, this);
	}

	@Override
	public String toString() {
		return getRawType().getTypeName();
	}
}