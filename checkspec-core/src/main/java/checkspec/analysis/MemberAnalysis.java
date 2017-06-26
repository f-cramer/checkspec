package checkspec.analysis;

import java.lang.reflect.Member;
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

import com.google.common.collect.Lists;

import checkspec.report.Report;
import checkspec.spec.ClassSpecification;
import checkspec.spec.Specification;
import checkspec.spring.ResolvableType;

public abstract class MemberAnalysis<MemberType extends Member, SpecificationType extends Specification<MemberType>, ReportType extends Report<MemberType, SpecificationType>>
		implements AnalysisForClass<Collection<? extends ReportType>> {

	private static final SimilarityScore<Integer> NAME_DISTANCE = LevenshteinDistance.getDefaultInstance();
	protected static final MemberVisibilityAnalysis VISIBILITY_ANALYSIS = new MemberVisibilityAnalysis();
	protected static final MemberModifiersAnalysis MODIFIERS_ANALYSIS = new MemberModifiersAnalysis();

	private final Comparator<ReportType> comparator = Comparator.comparing(report -> report.getSpec().getName());

	@Override
	public final Collection<? extends ReportType> analyze(ResolvableType type, ClassSpecification spec) {
		Class<?> clazz = type.getRawClass();
		SpecificationType[] specifications = getMemberSpecifications(spec);

		List<ReportType> reports = Arrays.stream(specifications).parallel()
				.flatMap(getMapperFunction(clazz))
				.map(pair -> checkMember(pair.getLeft(), pair.getRight()))
				.sorted(Comparator.comparingInt(Report::getScore))
				.collect(Collectors.toList());

		List<MemberType> unusedMembers = Lists.newArrayList(getMembers(clazz));
		List<SpecificationType> notFoundSpecs = Lists.newArrayList(specifications);

		Iterator<ReportType> iterator = reports.iterator();
		while (iterator.hasNext()) {
			ReportType report = iterator.next();
			MemberType member = report.getImplementation();
			SpecificationType memberSpec = report.getSpec();
			if (unusedMembers.contains(member) && notFoundSpecs.contains(memberSpec)) {
				unusedMembers.remove(member);
				notFoundSpecs.remove(memberSpec);
			} else {
				iterator.remove();
			}
		}

		Stream<ReportType> foundMembers = reports.parallelStream();
		Stream<ReportType> notFoundMembers = notFoundSpecs.parallelStream()
				.map(this::createEmptyReport);

		return Stream.concat(foundMembers, notFoundMembers)
				.sorted(comparator)
				.collect(Collectors.toList());
	}

	private Function<SpecificationType, Stream<Pair<MemberType, SpecificationType>>> getMapperFunction(Class<?> clazz) {
		return spec -> Arrays.stream(getMembers(clazz)).parallel()
				.map(member -> Pair.of(member, spec));
	}

	protected abstract SpecificationType[] getMemberSpecifications(ClassSpecification spec);

	protected abstract MemberType[] getMembers(Class<?> clazz);

	protected abstract ReportType checkMember(MemberType member, SpecificationType specification);

	protected abstract ReportType createEmptyReport(SpecificationType specification);

	protected Comparator<ReportType> getComparator() {
		return comparator;
	}

	protected final int calculateDistance(String left, String right) {
		Integer distance = NAME_DISTANCE.apply(left, right);
		return distance == null ? Integer.MAX_VALUE : Math.abs(distance);
	}
}
