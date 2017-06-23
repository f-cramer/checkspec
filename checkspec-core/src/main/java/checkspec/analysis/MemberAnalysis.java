package checkspec.analysis;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.SimilarityScore;

import checkspec.report.Report;
import checkspec.spec.ClassSpecification;
import checkspec.spec.Specification;
import checkspec.spring.ResolvableType;

public abstract class MemberAnalysis<MemberType extends Member, SpecificationType extends Specification<MemberType>, ReportType extends Report<? extends Specification<MemberType>, MemberType>>
		implements AnalysisForClass<Collection<? extends ReportType>> {

	protected static final SimilarityScore<Integer> NAME_SIMILARITY = LevenshteinDistance.getDefaultInstance();
	protected static final MemberVisibilityAnalysis VISIBILITY_ANALYSIS = new MemberVisibilityAnalysis();
	protected static final MemberModifiersAnalysis MODIFIERS_ANALYSIS = new MemberModifiersAnalysis();

	@Override
	public Collection<? extends ReportType> analyse(ResolvableType type, ClassSpecification spec) {
		Class<?> clazz = type.getRawClass();
		SpecificationType[] specifications = getMemberSpecifications(spec);

		List<Pair<MemberType, SpecificationType>> pairs = Arrays.stream(specifications).parallel()
				.flatMap(getMapperFunction(clazz))
				.sorted(Comparator.comparingInt(pair -> getDistance(pair.getLeft(), pair.getRight())))
				.collect(Collectors.toList());

		List<MemberType> unusedMembers = new ArrayList<>(Arrays.asList(getMembers(clazz)));
		List<SpecificationType> notFoundSpecs = new ArrayList<>(Arrays.asList(specifications));

		Iterator<Pair<MemberType, SpecificationType>> iterator = pairs.iterator();
		while (iterator.hasNext()) {
			Pair<MemberType, SpecificationType> pair = iterator.next();
			MemberType member = pair.getLeft();
			SpecificationType memberSpec = pair.getRight();
			if (unusedMembers.contains(member) && notFoundSpecs.contains(memberSpec)) {
				unusedMembers.remove(member);
				notFoundSpecs.remove(memberSpec);
			} else {
				iterator.remove();
			}
		}

		Stream<ReportType> foundMethods = pairs.parallelStream()
				.map(pair -> checkMember(pair.getLeft(), pair.getRight()));
		Stream<ReportType> notFoundMethods = notFoundSpecs.parallelStream()
				.map(this::createEmptyReport);

		return Stream.concat(foundMethods, notFoundMethods)
				.sorted(getComparator())
				.collect(Collectors.toList());
	}

	private Function<SpecificationType, Stream<Pair<MemberType, SpecificationType>>> getMapperFunction(Class<?> clazz) {
		return spec -> Arrays.stream(getMembers(clazz)).parallel()
				.map(member -> Pair.of(member, spec));
	}

	protected abstract SpecificationType[] getMemberSpecifications(ClassSpecification spec);

	protected abstract int getDistance(MemberType member, SpecificationType specification);

	protected abstract MemberType[] getMembers(Class<?> clazz);

	protected abstract ReportType checkMember(MemberType member, SpecificationType specification);

	protected abstract ReportType createEmptyReport(SpecificationType specification);

	protected abstract Comparator<ReportType> getComparator();
}
