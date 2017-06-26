package checkspec.extension;

public interface Extension<RawElement, ExtensionPoint> {

	void extend(RawElement rawElement, ExtensionPoint extensionPoint);
}
