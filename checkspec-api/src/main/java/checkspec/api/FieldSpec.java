package checkspec.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface FieldSpec {

	State isFinal() default State.NOT_SPECIFIED;

	State isStatic() default State.NOT_SPECIFIED;

	State isStrict() default State.NOT_SPECIFIED;

	State isVolatile() default State.NOT_SPECIFIED;

	State isTransient() default State.NOT_SPECIFIED;
}
