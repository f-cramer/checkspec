package checkspec.type;

import javassist.Modifier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ModifiersSpec {
	
	private final int modifiers;
    
    public boolean isPublic() {
    	return Modifier.isPublic(modifiers);
    }

    public boolean isPrivate() {
    	return Modifier.isPrivate(modifiers);
    }

    public boolean isProtected() {
    	return Modifier.isProtected(modifiers);
    }

    public boolean isPackage() {
    	return Modifier.isPackage(modifiers);
    }
    
    public Visibility getType() {
    	if (isPublic()) {
    		return Visibility.PUBLIC;
    	}
    	
    	if (isPrivate()) {
    		return Visibility.PRIVATE;
    	}
    	
    	if (isProtected()) {
    		return Visibility.PROTECTED;
    	}
    	
    	if (isPackage()) {
    		return Visibility.DEFAULT;
    	}
    	
    	return null;
    }

    public boolean isStatic() {
    	return Modifier.isStatic(modifiers);
    }

    public boolean isFinal() {
    	return Modifier.isFinal(modifiers);
    }

    public boolean isSynchronized() {
    	return Modifier.isSynchronized(modifiers);
    }

    public boolean isVolatile() {
    	return Modifier.isVolatile(modifiers);
    }

    public boolean isTransient() {
    	return Modifier.isTransient(modifiers);
    }

    public boolean isNative() {
    	return Modifier.isNative(modifiers);
    }

    public boolean isInterface() {
    	return Modifier.isInterface(modifiers);
    }

    public boolean isAnnotation() {
    	return Modifier.isAnnotation(modifiers);
    }

    public boolean isEnum() {
    	return Modifier.isEnum(modifiers);
    }

    public boolean isAbstract() {
    	return Modifier.isAbstract(modifiers);
    }

    public boolean isStrict() {
    	return Modifier.isStrict(modifiers);
    }

	public static ModifiersSpec from(int modifiers) {
		return new ModifiersSpec(modifiers);
	}
}
