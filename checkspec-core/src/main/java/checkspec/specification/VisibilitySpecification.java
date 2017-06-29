package checkspec.specification;

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
public class VisibilitySpecification implements Specification<Integer> {

	@NonNull
	private final Visibility[] visibilities;
	
	private final Integer rawElement;

	public VisibilitySpecification(int modifiers, Annotation[] annotations) {
		Visibility[] vis = Arrays.stream(annotations)
				.filter(Spec.class::isInstance)
				.map(Spec.class::cast)
				.findAny()
				.map(Spec::visibility)
				.orElseGet(() -> fromModifiers(modifiers));

		if (vis.length == 0) {
			vis = fromModifiers(modifiers);
		}

		visibilities = vis;
		rawElement = modifiers;
	}
	
	public boolean matches(Visibility visibility) {
		return Arrays.stream(visibilities).anyMatch(e -> e == Visibility.INSIGNIFICANT || e == visibility);
	}
	
	private Visibility[] fromModifiers(int modifiers) {
		return new Visibility[] { MemberUtils.getVisibility(modifiers) };
	}

	@Override
	public String getName() {
		return "";
	}
}
