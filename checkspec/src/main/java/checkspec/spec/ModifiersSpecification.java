package checkspec.spec;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

import checkspec.api.Modifiers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true, chain = false)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ModifiersSpecification {

	private final State isAbstract;
	private final State isFinal;
	private final boolean isInterface;
	private final State isNative;
	private final State isStatic;
	private final State isStrict;
	private final State isSynchronized;
	private final State isTransient;
	private final State isVolatile;

	public static ModifiersSpecification from(int modifiers, Annotation[] annotations) {
		State isAbstract = get(modifiers, Modifier::isAbstract, annotations, Modifiers::isAbstract);
		State isFinal = get(modifiers, Modifier::isFinal, annotations, Modifiers::isFinal);
		boolean isInterface = Modifier.isInterface(modifiers);
		State isNative = get(modifiers, Modifier::isNative, annotations, Modifiers::isNative);
		State isStatic = get(modifiers, Modifier::isStatic, annotations, Modifiers::isStatic);
		State isStrict = get(modifiers, Modifier::isStrict, annotations, Modifiers::isStrict);
		State isSynchronized = get(modifiers, Modifier::isSynchronized, annotations, Modifiers::isSynchronized);
		State isTransient = get(modifiers, Modifier::isTransient, annotations, Modifiers::isTransient);
		State isVolatile = get(modifiers, Modifier::isVolatile, annotations, Modifiers::isVolatile);

		return new ModifiersSpecification(isAbstract, isFinal, isInterface, isNative, isStatic, isStrict, isSynchronized, isTransient, isVolatile);
	}

	private static State get(int modifiers, IntToBooleanFunction booleanFunction, Annotation[] annotations, Function<Modifiers, checkspec.api.State> stateFunction) {
		//@formatter:off
		return Arrays.stream(annotations)
		             .parallel()
		             .filter(Modifiers.class::isInstance)
		             .map(Modifiers.class::cast)
		             .findAny()
		             .map(stateFunction::apply)
		             .map(ModifiersSpecification::from)
		             .orElseGet(() -> from(booleanFunction.apply(modifiers)));
		//@formatter:on
	}

	private static State from(checkspec.api.State state) {
		switch (state) {
		case TRUE:
			return State.TRUE;
		case FALSE:
			return State.FALSE;
		case INSIGNIFICANT:
			return State.INSIGNFICANT;
		case NOT_SPECIFIED:
			return null;
		}

		return null;
	}
	
	private static State from(boolean state) {
		return state ? State.TRUE : State.FALSE;
	}

	public static enum State {
		TRUE(e -> e), FALSE(e -> !e), INSIGNFICANT(e -> true);

		private final BooleanToBooleanFunction matchingFunction;

		private State(BooleanToBooleanFunction matchingFunction) {
			this.matchingFunction = Objects.requireNonNull(matchingFunction);
		}

		public final boolean matches(boolean state) {
			return matchingFunction.apply(state);
		}
	}

	private static interface IntToBooleanFunction {

		boolean apply(int b);
	}

	private static interface BooleanToBooleanFunction {

		boolean apply(boolean b);
	}
}
