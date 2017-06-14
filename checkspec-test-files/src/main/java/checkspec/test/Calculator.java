package checkspec.test;

import checkspec.api.Modifiers;
import checkspec.api.Spec;
import checkspec.api.State;

@Spec(modifiers = @Modifiers(isAbstract = State.FALSE))
@SuppressWarnings("unused")
public abstract class Calculator {

	private int abc;

	@Spec(modifiers = @Modifiers(isAbstract = State.FALSE))
	public abstract int add(int a, int b);

	@Spec(modifiers = @Modifiers(isAbstract = State.FALSE))
	public abstract int subtract(int a, int b);

	@Spec(modifiers = @Modifiers(isAbstract = State.FALSE))
	public abstract int multiply(int a, int b);

	@Spec(modifiers = @Modifiers(isAbstract = State.FALSE))
	public abstract int divide(int a, int b);
}
