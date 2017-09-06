package checkspec.util;

import java.util.Optional;

public enum MatchingState {

	FULL_MATCH, PARTIAL_MATCH, NO_MATCH;

	public <T, U extends T, V extends T, W extends T> T evaluate(U full, V partial, W no) {
		switch (this) {
		case FULL_MATCH:
			return full;
		case PARTIAL_MATCH:
			return partial;
		case NO_MATCH:
			return no;
		}
		throw new AssertionError();
	}

	public MatchingState merge(MatchingState state) {
		return ordinal() <= state.ordinal() ? state : this;
	}

	public MatchingState merge(Optional<MatchingState> optionalState) {
		return optionalState.map(this::merge).orElse(this);
	}
}
