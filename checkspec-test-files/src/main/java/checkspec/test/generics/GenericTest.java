package checkspec.test.generics;

import checkspec.api.Spec;

@Spec
public interface GenericTest<T> {

	public void print(T t);
}
