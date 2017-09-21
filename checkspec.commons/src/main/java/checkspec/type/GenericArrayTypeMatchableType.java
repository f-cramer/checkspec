package checkspec.type;

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



import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.util.Optional;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.util.MatchingState;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A {@link MatchableType} that was created from an instance of
 * {@link GenericArrayType}.
 *
 * @author Florian Cramer
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class GenericArrayTypeMatchableType extends AbstractMatchableType<GenericArrayType, GenericArrayTypeMatchableType> {

	private final MatchableType componentType;

	GenericArrayTypeMatchableType(final GenericArrayType rawType) {
		super(GenericArrayTypeMatchableType.class, rawType);
		this.componentType = MatchableType.forType(rawType.getGenericComponentType());
	}

	@Override
	protected Optional<MatchingState> matchesImpl(GenericArrayTypeMatchableType type, MultiValuedMap<Class<?>, Class<?>> matches) {
		MatchableType oComponentType = type.getComponentType();
		return Optional.of(componentType.matches(oComponentType, matches));
	}

	@Override
	public Class<?> getRawClass() {
		Class<?> componentTypeRawClass = componentType.getRawClass();
		return Array.newInstance(componentTypeRawClass, 0).getClass();
	}
}
