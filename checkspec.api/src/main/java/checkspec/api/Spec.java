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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that a specification should be created from the given element and
 * all of its sub elements.
 *
 * @author Florian Cramer
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD })
public @interface Spec {

	/**
	 * Should be set to false if an element should not be included into the
	 * generated specification.
	 *
	 * @return whether or not the annotated element is part of the specification
	 */
	boolean value() default true;

	/**
	 * Returns the specified visibility of the annotated element.
	 *
	 * @return the visibility of the annotated element
	 */
	Visibility[] visibility() default {};

	/**
	 * Returns the specified modifiers for the annotated element.
	 *
	 * @return the modifiers for the annotated element
	 */
	Modifiers modifiers() default @Modifiers;
}
