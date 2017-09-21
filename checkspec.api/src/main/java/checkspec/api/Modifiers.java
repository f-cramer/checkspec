package checkspec.api;

/*-
 * #%L
 * CheckSpec API
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

/**
 * Way for setting each modifier for a given specification. Modifiers that are
 * not supported on a specific element (i.e. {@code abstract} for a field) are
 * being ignored.
 *
 * @author Florian Cramer
 *
 */
public @interface Modifiers {

	/**
	 * Returns whether or not the element has modifier {@code abstract}.
	 *
	 * @return whether or not the element is abstract
	 */
	State isAbstract() default State.NOT_SPECIFIED;

	/**
	 * Returns whether or not the element has modifier {@code final}.
	 *
	 * @return whether or not the element is final
	 */
	State isFinal() default State.NOT_SPECIFIED;

	/**
	 * Returns whether or not the element has modifier {@code native}.
	 *
	 * @return whether or not the element is native
	 */
	State isNative() default State.NOT_SPECIFIED;

	/**
	 * Returns whether or not the element has modifier {@code static}.
	 *
	 * @return whether or not the element is static
	 */
	State isStatic() default State.NOT_SPECIFIED;

	/**
	 * Returns whether or not the element has modifier {@code strictfp}.
	 *
	 * @return whether or not the element is strict
	 */
	State isStrict() default State.NOT_SPECIFIED;

	/**
	 * Returns whether or not the element has modifier {@code synchronized}.
	 *
	 * @return whether or not the element is synchronized
	 */
	State isSynchronized() default State.NOT_SPECIFIED;

	/**
	 * Returns whether or not the element has modifier {@code volatile}.
	 *
	 * @return whether or not the element is volatile
	 */
	State isVolatile() default State.NOT_SPECIFIED;

	/**
	 * Returns whether or not the element has modifier {@code transient}.
	 *
	 * @return whether or not the element is transient
	 */
	State isTransient() default State.NOT_SPECIFIED;
}
