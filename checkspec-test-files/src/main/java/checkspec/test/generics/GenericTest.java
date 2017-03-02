package checkspec.test.generics;

import checkspec.annotation.Spec;

@Spec
public interface GenericTest<T> {

	public void print(T t);
}
