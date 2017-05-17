package checkspec.spec;

public interface Specification<Raw> {

	public String getName();

	public Raw getRawElement();

	public ModifiersSpecification getModifiers();

	public VisibilitySpecification getVisibility();
}
