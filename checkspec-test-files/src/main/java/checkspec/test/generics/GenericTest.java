package checkspec.test.generics;

import checkspec.api.Spec;

@Spec
public interface GenericTest<T extends Object> {

	public void print(T t);
}
