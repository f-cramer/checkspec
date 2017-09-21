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

import java.lang.annotation.Annotation;
import java.util.List;

import checkspec.extension.AbstractExtendable;
import checkspec.util.TypeDiscovery;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class PackageSpecification extends AbstractExtendable<PackageSpecification, Package> implements Specification<Package> {

	private static final PackageSpecificationExtension[] EXTENSIONS;

	static {
		List<PackageSpecificationExtension> instances = TypeDiscovery.getUniqueInstancesOf(PackageSpecificationExtension.class);
		EXTENSIONS = instances.toArray(new PackageSpecificationExtension[instances.size()]);
	}

	@NonNull
	private final String name;

	@NonNull
	private final Package rawElement;

	public PackageSpecification(Package pkg, Annotation[] annotations) {
		rawElement = pkg;
		name = pkg.getName();

		performExtensions(EXTENSIONS, this, rawElement);
	}
}
