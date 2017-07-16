package checkspec.report;

import java.lang.reflect.Member;

import checkspec.specification.Specification;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public abstract class MemberReport<MemberType extends Member, SpecificationType extends Specification<MemberType>> extends Report<MemberType, SpecificationType> {

	protected MemberReport(SpecificationType spec) {
		super(spec);
	}

	protected MemberReport(SpecificationType spec, MemberType implementation) {
		super(spec, implementation);
	}

}
