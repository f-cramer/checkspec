package checkspec.processor;

import checkspec.api.Spec;
import checkspec.spec.Specification;

public class SpecAnnotationProcessor implements AnnotationProcessor<Spec> {

	@Override
	public boolean wantToProcess(Specification<?> specification) {
		return true;
	}

	@Override
	public void process(Specification<?> specification, Spec annotation) {
		
	}

}
