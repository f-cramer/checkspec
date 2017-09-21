package checkspec.api;

/*-
 * #%L
 * CheckSpec API
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

/**
 * Represents the state of a modifier.
 *
 * @author Florian Cramer
 *
 */
public enum State {

	/**
	 * The modifier has to be set.
	 */
	TRUE,
	/**
	 * The modifier must not be set
	 */
	FALSE,
	/**
	 * The modifier can either be set or not.
	 */
	IRRELEVANT,
	/**
	 * Default value, should not be used directly.
	 */
	NOT_SPECIFIED
}
