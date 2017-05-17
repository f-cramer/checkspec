package checkspec.spec;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Optional;

import checkspec.api.Spec;
import checkspec.api.Visibility;
import checkspec.util.MemberUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class VisibilitySpecification {

	@NonNull
	private final Visibility[] visibilities;

	public boolean matches(Visibility visibility) {
		return Arrays.stream(visibilities).anyMatch(e -> e == Visibility.INSIGNIFICANT || e == visibility);
	}

	public static VisibilitySpecification from(int modifiers, Annotation[] annotations) {
		//@formatter:off
		Optional<Visibility[]> specs = Arrays.stream(annotations)
		                                     .filter(e -> e instanceof Spec)
		                                     .map(e -> (Spec) e)
		                                     .findAny()
		                                     .map(Spec::visibility);
		//@formatter:on

		return new VisibilitySpecification(specs.orElseGet(() -> new Visibility[] { MemberUtils.getVisibility(modifiers) }));
	}
}
