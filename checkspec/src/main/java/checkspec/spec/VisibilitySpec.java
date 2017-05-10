package checkspec.spec;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Optional;

import checkspec.api.Spec;
import checkspec.api.Visibility;
import checkspec.util.MemberUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VisibilitySpec {
	
	@NonNull
	private final Visibility[] visibilities; 
	
	public boolean matches(Visibility visibility) {
		return Arrays.stream(visibilities).anyMatch(e -> e == Visibility.INSIGNIFICANT || e == visibility);
	}
	
	public static VisibilitySpec from(int modifiers, Annotation[] annotations) {
		//@formatter:off
		Optional<Visibility[]> specs = Arrays.stream(annotations)
		                                     .filter(e -> e instanceof Spec)
		                                     .map(e -> (Spec) e)
		                                     .findAny()
		                                     .map(Spec::visibility);
		//@formatter:on
	
		return new VisibilitySpec(specs.orElseGet(() -> new Visibility[] {MemberUtils.getVisibility(modifiers)}));
	}
}
