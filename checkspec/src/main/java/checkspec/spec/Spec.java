package checkspec.spec;

public interface Spec<Raw> {
	
	public String getName();
	
	public Raw getRawElement();
	
	public ModifiersSpec getModifiers();
	
	public VisibilitySpec getVisibility();
}
