package checkspec.examples.example2;

import checkspec.api.Spec;

@Spec
public interface GenericTest<T extends Object> {

	public void print(T t);
}