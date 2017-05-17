package checkspec.spec;

public interface Specification<Raw> {

	public String getName();

	public Raw getRawElement();

	public ModifiersSpec getModifiers();

	public VisibilitySpec getVisibility();
}
