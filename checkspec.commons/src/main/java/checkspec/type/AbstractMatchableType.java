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



import static java.util.function.Function.*;
import static java.util.stream.Collectors.*;
import static org.apache.commons.lang3.ClassUtils.*;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.util.MatchingState;
import checkspec.util.TypeDiscovery;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@EqualsAndHashCode
abstract class AbstractMatchableType<RawType extends Type, Self extends AbstractMatchableType<RawType, Self>> implements MatchableType {

	private static List<Matcher> MATCHERS = TypeDiscovery.getUniqueInstancesOf(Matcher.class);

	private static Stream<Matcher> getMatchers(Class<? extends MatchableType> left, Class<? extends MatchableType> right) {
		return MATCHERS.stream()
				.filter(createFilter(left, right));
	}

	private static Predicate<Matcher> createFilter(Class<? extends MatchableType> left, Class<? extends MatchableType> right) {
		return matcher -> {
			MatchableTypePair[] pairs = matcher.getMatchables();
			return Arrays.stream(pairs)
					.anyMatch(pair -> isAssignable(left, pair.getLeft()) && isAssignable(right, pair.getRight()));
		};
	}

	@Getter
	protected RawType rawType;
	private Class<Self> self;

	protected AbstractMatchableType(@NonNull final Class<Self> self, @NonNull final RawType rawType) {
		this.self = self;
		this.rawType = rawType;
		MatchableTypeCache.put(rawType, this);
	}

	@Override
	public final MatchingState matches(MatchableType type, MultiValuedMap<Class<?>, Class<?>> matches) {
		if (equals(type)) {
			return MatchingState.FULL_MATCH;
		}

		Optional<MatchingState> optionalMatch = Optional.empty();
		if (self.isInstance(type)) {
			optionalMatch = matchesImpl(self.cast(type), matches);
		}
		return optionalMatch.orElseGet(() -> matchesWithMatchers(type, matches));
	}

	protected abstract Optional<MatchingState> matchesImpl(Self type, MultiValuedMap<Class<?>, Class<?>> matches);

	private MatchingState matchesWithMatchers(@NonNull MatchableType type, MultiValuedMap<Class<?>, Class<?>> matches) {
		Stream<Matcher> matchers = getMatchers(getClass(), type.getClass());
		Map<MatchingState, Long> frequencyMap = matchers.map(m -> m.matches(this, type, matches))
				.collect(groupingBy(identity(), () -> new EnumMap<>(MatchingState.class), counting()));

		if (frequencyMap.isEmpty()) {
			return MatchingState.NO_MATCH;
		} else if (frequencyMap.size() == 1) {
			return frequencyMap.keySet().iterator().next();
		} else {
			List<Entry<MatchingState, Long>> sortedFrequencyMap = frequencyMap.entrySet().stream()
					.sorted(Entry.comparingByValue(Comparator.reverseOrder()))
					.collect(toList());
			Long maxFrequency = sortedFrequencyMap.get(0).getValue();
			return sortedFrequencyMap.stream()
					.filter(e -> e.getValue() == maxFrequency)
					.max(Comparator.comparing(Entry::getValue))
					.map(Entry::getKey)
					.get();
		}
	}

	@Override
	public String toString() {
		return getRawType().getTypeName();
	}
}
