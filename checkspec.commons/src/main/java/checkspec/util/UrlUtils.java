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

import java.net.URL;

import lombok.NonNull;

/**
 * Miscellaneous methods that are working on instances of {@link URL}. Mainly
 * for internal use within the framework itself.
 *
 * @author Florian Cramer
 * @see URL
 */
public class UrlUtils {

	/**
	 * Returns whether or not the path of {@code child} starts with the path of
	 * {@code parent}, i.e. if {@code parent} is a parent to {@code child}.
	 *
	 * @param child
	 *            the child
	 * @param parent
	 *            the parent
	 * @return whether or not {@code parent} is parent to {@code child}
	 */
	public static boolean isParent(@NonNull URL child, @NonNull URL parent) {
		return child.getPath().startsWith(parent.getPath());
	}
}
