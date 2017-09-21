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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * An abstract implementation of {@link Extendable} that provides methods to
 * easily add and get extensions.
 *
 * @author Florian Cramer
 *
 * @param <ExtensionPoint>
 *            the extension type
 * @param <Payload>
 *            the payload type
 */
@Getter
@ToString
@EqualsAndHashCode
public class AbstractExtendable<ExtensionPoint extends Extendable, Payload> implements Extendable {

	private final Map<Class<?>, Object> extensions = new HashMap<>();

	@Override
	@SuppressWarnings("unchecked")
	public <T> Optional<T> addExtension(@NonNull T object) {
		Class<?> clazz = object.getClass();
		return (Optional<T>) Optional.ofNullable(extensions.put(clazz, object));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Optional<T> getExtension(@NonNull Class<T> clazz) {
		return (Optional<T>) Optional.ofNullable(extensions.get(clazz));
	}

	protected void performExtensions(Extension<ExtensionPoint, Payload>[] extensions, ExtensionPoint extensionPoint, Payload payload) {
		for (Extension<ExtensionPoint, Payload> extension : extensions) {
			try {
				extension.extend(extensionPoint, payload);
			} catch (Exception expected) {
			}
		}
	}
}
