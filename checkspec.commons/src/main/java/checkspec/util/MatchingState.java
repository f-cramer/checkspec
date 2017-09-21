package checkspec.util;

import java.util.Optional;

/**
 * Represents to what degree much two types match each other.
 *
 * @author Florian Cramer
 *
 */
public enum MatchingState {

	FULL_MATCH, PARTIAL_MATCH, NO_MATCH;

	/**
	 * Returns one of the three parameters depending on the {@code this}.
	 * <ul>
	 * <li>if {@code this == FULL_MATCH} {@code full} is returned</li>
	 * <li>if {@code this == PARTIAL_MATCH} {@code partial} is returned</li>
	 * <li>if {@code this == NO_MATCH} {@code no} is returned</li>
	 * </ul>
	 *
	 * @param full
	 *            the parameter to return for {@link FULL_MATCH}
	 * @param partial
	 *            the parameter to return for {@link PARTIAL_MATCH}
	 * @param no
	 *            the parameter to return for {@link NO_MATCH}
	 * @param <T>
	 *            the return type
	 * @param <U>
	 *            the first argument type
	 * @param <V>
	 *            the second argument type
	 * @param <W>
	 *            the third argument type
	 * @return one of the three parameters depending on {@code this}
	 */
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

	/**
	 * Merges {@code this} with the given state. The worse of both is returned.
	 *
	 * @param state
	 *            the other state
	 * @return the worse of both states
	 */
	public MatchingState merge(MatchingState state) {
		return ordinal() <= state.ordinal() ? state : this;
	}

	/**
	 * Merges {@code this} with the given state. If the given state is present
	 * the worse of both is returned, otherwise {@code this}.
	 *
	 * @param optionalState
	 *            the other state
	 * @return the worse of both states or {@code this} if no state was given
	 */
	public MatchingState merge(Optional<MatchingState> optionalState) {
		return optionalState.map(this::merge).orElse(this);
	}
}
