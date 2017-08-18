package checkspec.type;

import java.lang.reflect.Type;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@EqualsAndHashCode
abstract class AbstractMatchableType<RawType extends Type> implements MatchableType {

	protected RawType rawType;

	protected AbstractMatchableType(@NonNull final RawType rawType) {
		this.rawType = rawType;
		MatchableTypeCache.put(rawType, this);
	}

	@Override
	public String toString() {
		return getRawType().getTypeName();
	}
}