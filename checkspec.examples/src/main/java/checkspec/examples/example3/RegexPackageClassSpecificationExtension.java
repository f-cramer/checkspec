package checkspec.examples.example3;

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

import java.util.regex.Pattern;

import checkspec.specification.ClassSpecification;
import checkspec.specification.ClassSpecificationExtension;
import checkspec.type.MatchableType;

public class RegexPackageClassSpecificationExtension implements ClassSpecificationExtension {

	@Override
	public void extend(ClassSpecification extensionPoint, MatchableType payload) {
		Class<?> clazz = payload.getRawClass();
		RegexPackage regexPackage = clazz.getAnnotation(RegexPackage.class);

		if (regexPackage == null) {
			Package pkg = clazz.getPackage();
			regexPackage = pkg.getAnnotation(RegexPackage.class);
		}

		if (regexPackage != null) {
			String regex = regexPackage.value();
			int flags = regexPackage.flags();

			Pattern pattern = Pattern.compile(regex, flags);
			extensionPoint.addExtension(new RegexPackageSpecification(pattern));
		}
	}
}
