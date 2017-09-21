package checkspec.examples.example1;

/*-
 * #%L
 * CheckSpec Examples
 * %%
 * Copyright (C) 2017 Florian Cramer
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



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
