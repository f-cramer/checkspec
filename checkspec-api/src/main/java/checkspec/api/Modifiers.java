package checkspec.api;

public @interface Modifiers {

	State isAbstract() default State.NOT_SPECIFIED;

	State isFinal() default State.NOT_SPECIFIED;

	State isNative() default State.NOT_SPECIFIED;

	State isStatic() default State.NOT_SPECIFIED;

	State isStrict() default State.NOT_SPECIFIED;

	State isSynchronized() default State.NOT_SPECIFIED;

	State isVolatile() default State.NOT_SPECIFIED;

	State isTransient() default State.NOT_SPECIFIED;
}
