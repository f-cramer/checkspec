package checkspec.spec;

import checkspec.spring.ResolvableType;
import checkspec.util.ClassUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
public class InterfaceSpecification {

	
	@NonNull
	private final String name;
	
	@NonNull
	private final ResolvableType rawElement;
	
	public InterfaceSpecification(Class<?> interf) {
		this.name = ClassUtils.getName(interf);
		rawElement = ResolvableType.forClass(interf);
	}
}
