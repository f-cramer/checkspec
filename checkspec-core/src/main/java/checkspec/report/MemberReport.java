package checkspec.report;

import java.lang.reflect.Member;

import checkspec.spec.Specification;

public abstract class MemberReport<MemberType extends Member, SpecificationType extends Specification<MemberType>> extends Report<MemberType, SpecificationType> {

	protected MemberReport(SpecificationType spec) {
		super(spec);
	}
	
	protected MemberReport(SpecificationType spec, MemberType implementation) {
		super(spec, implementation);
	}

}
