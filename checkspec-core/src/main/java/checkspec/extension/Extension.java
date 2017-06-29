package checkspec.extension;

public interface Extension<ExtensionPoint extends Extendable, Payload> {

	void extend(ExtensionPoint extensionPoint, Payload payload);
}
