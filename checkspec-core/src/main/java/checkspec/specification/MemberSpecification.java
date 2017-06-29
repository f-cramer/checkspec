package checkspec.specification;

import java.lang.reflect.Member;

public interface MemberSpecification<MemberType extends Member> extends Specification<MemberType> {

	public ModifiersSpecification getModifiers();

	public VisibilitySpecification getVisibility();
}
