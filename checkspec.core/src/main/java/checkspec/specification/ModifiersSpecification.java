package checkspec.specification;

/*-
 * #%L
 * CheckSpec Core
 * %%
 * Copyright (C) 2017 Florian Cramer
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import checkspec.api.Modifiers;
import checkspec.api.Spec;
import checkspec.extension.AbstractExtendable;
import checkspec.util.TypeDiscovery;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * A specification of modifiers of a type or class element.
 *
 * @author Florian Cramer
 *
 */
@Value
@Accessors(fluent = true, chain = false)
@EqualsAndHashCode(callSuper = true)
public class ModifiersSpecification extends AbstractExtendable<ModifiersSpecification, Integer> implements Specification<Integer> {

	private static final ModifiersSpecificationExtension[] EXTENSIONS;

	static {
		List<ModifiersSpecificationExtension> instances = TypeDiscovery.getUniqueInstancesOf(ModifiersSpecificationExtension.class);
		EXTENSIONS = instances.toArray(new ModifiersSpecificationExtension[instances.size()]);
	}

	private final State isAbstract;
	private final State isFinal;
	private final boolean isInterface;
	private final State isNative;
	private final State isStatic;
	private final State isStrict;
	private final State isSynchronized;
	private final State isTransient;
	private final State isVolatile;

	@Accessors(fluent = false, chain = false)
	private final Integer rawElement;

	/**
	 * Creates a new {@link ModifiersSpecification} from the given modifiers and
	 * annotations.
	 *
	 * @param modifiers
	 *            the modifiers
	 * @param annotations
	 *            the annotations
	 */
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

		rawElement = modifiers;

		performExtensions(EXTENSIONS, this, modifiers);
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

	@Override
	public String getName() {
		return "";
	}

	private static State from(checkspec.api.State state) {
		switch (state) {
		case TRUE:
			return State.TRUE;
		case FALSE:
			return State.FALSE;
		case IRRELEVANT:
			return State.INSIGNFICANT;
		case NOT_SPECIFIED:
			return null;
		}

		return null;
	}

	private static State from(boolean state) {
		return state ? State.TRUE : State.FALSE;
	}

	/**
	 * Represents the state of a specific modifier.
	 *
	 * @author Florian Cramer
	 *
	 */
	public static enum State {
		TRUE(e -> e), FALSE(e -> !e), INSIGNFICANT(e -> true);

		private final BooleanToBooleanFunction matchingFunction;

		private State(@NonNull BooleanToBooleanFunction matchingFunction) {
			this.matchingFunction = matchingFunction;
		}

		/**
		 * Returns whether or not the given boolean state matches this state.
		 *
		 * @param state
		 *            the boolean state
		 * @return whether or not the given boolean state matches this state
		 */
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
