package checkspec.extension;

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



import java.util.Optional;

/**
 * An implementation of this interface has the ability to be extended with data.
 *
 * @author Florian Cramer
 *
 */
public interface Extendable {

	/**
	 * Adds a new extension. Previously defined extensions of the same type will
	 * be deleted.
	 *
	 * @param object
	 *            the extension
	 * @param <T>
	 *            the extension type
	 * @return a previously defined extension of the same type
	 */
	<T> Optional<T> addExtension(T object);

	/**
	 * Returns a previously registered extension of the given type.
	 *
	 * @param clazz
	 *            the extension type
	 * @param <T>
	 *            the extension type
	 * @return the extension if one exists
	 */
	<T> Optional<T> getExtension(Class<T> clazz);
}
