package checkspec.extension;

import checkspec.spec.Extendable;

public interface Extension<RawElement, ExtensionPoint extends Extendable> {

	void extend(RawElement rawElement, ExtensionPoint extensionPoint);
}
