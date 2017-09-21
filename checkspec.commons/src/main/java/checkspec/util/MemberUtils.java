package checkspec.util;

/*-
 * #%L
 * CheckSpec Commons
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



import java.lang.reflect.Modifier;

import checkspec.api.Visibility;
import lombok.experimental.UtilityClass;

/**
 * Miscellaneous methods that are working on instances of
 * {@link java.lang.reflect.Member Member}. Mainly for internal use within the
 * framework itself.
 *
 * @author Florian Cramer
 * @see java.lang.reflect.Member Member
 */
@UtilityClass
public final class MemberUtils {

	/**
	 * Returns the visibility for the given modifiers using the methods in
	 * {@link Modifier}.
	 *
	 * @param modifiers
	 *            the modifiers
	 * @return the visibility
	 */
	public static Visibility getVisibility(int modifiers) {
		if (Modifier.isPrivate(modifiers)) {
			return Visibility.PRIVATE;
		} else if (Modifier.isPublic(modifiers)) {
			return Visibility.PUBLIC;
		} else if (Modifier.isProtected(modifiers)) {
			return Visibility.PROTECTED;
		} else {
			return Visibility.PACKAGE;
		}
	}
}
