package checkspec.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface ClassSpec {

	State isAbstract() default State.NOT_SPECIFIED;

	State isFinal() default State.NOT_SPECIFIED;
	
	State isStatic() default State.NOT_SPECIFIED;
}
