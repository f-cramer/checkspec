package checkspec.spec;

import checkspec.spring.ResolvableType;
import checkspec.util.ClassUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
public class SuperClassSpecification {

	@NonNull
	private final String name;

	@NonNull
	private final ResolvableType rawElement;

	public SuperClassSpecification(Class<?> superClass) {
		this.name = ClassUtils.getName(superClass);
		rawElement = ResolvableType.forClass(superClass);
	}
}
