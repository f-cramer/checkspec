package checkspec.type;

import java.lang.reflect.Member;

public interface MemberSpec<T extends Member> extends Spec<T> {

	public ModifiersSpec getModifiers();
}
