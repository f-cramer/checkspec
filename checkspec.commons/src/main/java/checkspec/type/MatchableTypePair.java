package checkspec.type;

import lombok.Value;

@Value
public class MatchableTypePair {

	private final Class<? extends MatchableType> left;
	private final Class<? extends MatchableType> right;
}
