package checkspec.util;

public enum MatchingState {

	FULL_MATCH, PARTIAL_MATCH, NO_MATCH;

	public <T> T evaluate(T full, T partial, T no) {
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
}
