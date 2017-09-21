package checkspec.cli.option;

/*-
 * #%L
 * CheckSpec CLI
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

import org.apache.commons.cli.Option;

/**
 * Represents a command line option.
 *
 * @author Florian Cramer
 *
 */
public interface CommandLineOption {

	/**
	 * Returns the {@link Option} this command line option was created from.
	 *
	 * @return the option this command line option was created from
	 */
	Option getOption();
}
