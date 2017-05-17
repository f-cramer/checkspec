package checkspec.spec;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import checkspec.spring.ResolvableType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MethodParameterSpecification {

	@NonNull
	private final String name;

	@NonNull
	private final ResolvableType type;

	@NonNull
	private final Parameter rawElement;
	
	public static MethodParameterSpecification from(Method method, int parameterIndex) {
		Parameter parameter = method.getParameters()[parameterIndex];
		String name = parameter.getName();
		ResolvableType type = ResolvableType.forMethodParameter(method, parameterIndex);
		
		return new MethodParameterSpecification(name, type, parameter);
	}
}
