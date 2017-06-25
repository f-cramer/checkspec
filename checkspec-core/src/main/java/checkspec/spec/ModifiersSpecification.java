package checkspec.spec;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Function;

import checkspec.api.Modifiers;
import checkspec.api.Spec;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true, chain = false)
@ToString
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

	public ModifiersSpecification(int modifiers, Annotation[] annotations) {
		isAbstract = get(modifiers, Modifier::isAbstract, annotations, Modifiers::isAbstract);
		isFinal = get(modifiers, Modifier::isFinal, annotations, Modifiers::isFinal);
		isInterface = Modifier.isInterface(modifiers);
		isNative = get(modifiers, Modifier::isNative, annotations, Modifiers::isNative);
		isStatic = get(modifiers, Modifier::isStatic, annotations, Modifiers::isStatic);
		isStrict = get(modifiers, Modifier::isStrict, annotations, Modifiers::isStrict);
		isSynchronized = get(modifiers, Modifier::isSynchronized, annotations, Modifiers::isSynchronized);
		isTransient = get(modifiers, Modifier::isTransient, annotations, Modifiers::isTransient);
		isVolatile = get(modifiers, Modifier::isVolatile, annotations, Modifiers::isVolatile);
	}

	private static State get(int modifiers, IntToBooleanFunction booleanFunction, Annotation[] annotations, Function<Modifiers, checkspec.api.State> stateFunction) {
		return Arrays.stream(annotations).parallel()
				.filter(Spec.class::isInstance)
				.map(Spec.class::cast)
				.findAny()
				.map(Spec::modifiers)
				.map(stateFunction)
				.map(ModifiersSpecification::from)
				.orElseGet(() -> from(booleanFunction.apply(modifiers)));
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

		private State(@NonNull BooleanToBooleanFunction matchingFunction) {
			this.matchingFunction = matchingFunction;
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