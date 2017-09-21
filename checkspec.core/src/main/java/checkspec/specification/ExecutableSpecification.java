package checkspec.specification;

/*-
 * #%L
 * CheckSpec Core
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



import java.lang.reflect.Executable;

/**
 * The base interface for all specifications of executables such as constructors
 * and methods.
 *
 * @author Florian Cramer
 *
 * @param <RawType>
 *            the raw type
 */
public interface ExecutableSpecification<RawType extends Executable> extends MemberSpecification<RawType> {

	/**
	 * Returns the parameters sub specification of this specification.
	 *
	 * @return the parameters specification
	 */
	ParametersSpecification getParameters();

	/**
	 * Returns the exceptions sub specifications of this specification.
	 *
	 * @return the exceptions specifications
	 */
	ExceptionSpecification[] getExceptions();
}
