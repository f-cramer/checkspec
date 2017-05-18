package checkspec.spec;

import java.lang.annotation.Annotation;
import java.util.Arrays;

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

	public VisibilitySpecification(int modifiers, Annotation[] annotations) {
		//@formatter:off
		visibilities = Arrays.stream(annotations)
		                     .filter(e -> e instanceof Spec)
		                     .map(e -> (Spec) e)
		                     .findAny()
		                     .map(Spec::visibility)
		                     .orElseGet(() -> new Visibility[] { MemberUtils.getVisibility(modifiers) });
		//@formatter:on
	}
}
