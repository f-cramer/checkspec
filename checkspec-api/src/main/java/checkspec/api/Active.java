package checkspec.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Active {

	/**
	 * Processor class name. Has to extend
	 * {@link checkspec.processor.AnnotationProcessor}
	 */
	String value();
}
