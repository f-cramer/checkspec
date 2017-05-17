package checkspec.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD })
public @interface Spec {

	/**
	 * @return if the annotated element is part of the specification
	 */
	boolean value() default true;

	Visibility[] visibility() default {};
	
	Modifiers modifiers() default @Modifiers;
}
