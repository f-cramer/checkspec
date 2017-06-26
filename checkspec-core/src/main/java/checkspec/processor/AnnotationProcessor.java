package checkspec.processor;

import java.lang.annotation.Annotation;

import checkspec.spec.Specification;

public interface AnnotationProcessor<AnnotationType extends Annotation> {

	boolean wantToProcess(Specification<?> specification);
	
	void process(Specification<?> specification, AnnotationType annotation);
}
