package checkspec.type;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.junit.Before;
import org.junit.Test;

import checkspec.util.MatchingState;

public class ResolvableTypeTest {

	private static final Class<?> stringClass = String.class;
	private static final Class<?> listClass = List.class;
	
	private ResolvableType stringType;
	private ResolvableType listType;
	private MultiValuedMap<Class<?>, Class<?>> matches;

	@Before
	public void setUp() throws Exception {
		stringType = ResolvableType.forClass(stringClass);
		listType = ResolvableType.forClass(listClass);
		matches = new HashSetValuedHashMap<>();
	}

	@Test
	public void intShouldPartiallyMatchInteger() {
		ResolvableType intType = ResolvableType.forClass(int.class);
		ResolvableType integerType = ResolvableType.forClass(Integer.class);
		assertEquals(MatchingState.PARTIAL_MATCH, intType.matches(integerType, matches));
	}

	@Test
	public void listStringShouldPartiallyMatchListExtendsString() throws NoSuchMethodException, SecurityException {
		Method getStringList = ListSupplier.class.getDeclaredMethod("getStringList");
		ResolvableType listStringType = ResolvableType.forMethodReturnType(getStringList);
		Method getExtendsStringList = ListSupplier.class.getDeclaredMethod("getExtendsStringList");
		ResolvableType listExtendsStringType = ResolvableType.forMethodReturnType(getExtendsStringList);

		assertEquals(MatchingState.PARTIAL_MATCH, listStringType.matches(listExtendsStringType, matches));
	}

	@Test
	public void listStringShouldPartiallyMatchListSuperString() throws NoSuchMethodException, SecurityException {
		Method getStringList = ListSupplier.class.getDeclaredMethod("getStringList");
		ResolvableType listStringType = ResolvableType.forMethodReturnType(getStringList);
		Method getSuperStringList = ListSupplier.class.getDeclaredMethod("getSuperStringList");
		ResolvableType listSuperStringType = ResolvableType.forMethodReturnType(getSuperStringList);

		assertEquals(MatchingState.PARTIAL_MATCH, listStringType.matches(listSuperStringType, matches));
	}

	@Test
	public void stringShouldMatchItself() {
		assertEquals(MatchingState.FULL_MATCH, stringType.matches(stringType, matches));
	}
	
	@Test
	public void stringShouldNotMatchList() {
		assertEquals(MatchingState.NO_MATCH, stringType.matches(listType, matches));
	}

	@Test
	public void stringShouldMatchListIfStringListPairInMatches() {
		matches.put(stringClass, listClass);
		assertEquals(MatchingState.FULL_MATCH, stringType.matches(listType, matches));
	}

	@Test
	public void listShouldMatchItself() {
		assertEquals(MatchingState.FULL_MATCH, listType.matches(listType, matches));
	}

	@Test
	public void listShouldNotMatchString() {
		assertEquals(MatchingState.NO_MATCH, listType.matches(stringType, matches));
	}

	@Test
	public void listShouldMatchStringIfListStringPairInMatches() {
		matches.put(listClass, stringClass);
		assertEquals(MatchingState.FULL_MATCH, listType.matches(stringType, matches));
	}

	@Test
	public void aListShouldBeMatchingBListIfAMatchesB() throws NoSuchMethodException, SecurityException {
		Method aList = A.class.getDeclaredMethod("list");
		Method bList = B.class.getDeclaredMethod("list");

		ResolvableType aListType = ResolvableType.forMethodReturnType(aList);
		ResolvableType bListType = ResolvableType.forMethodReturnType(bList);

		matches.put(A.class, B.class);
		assertEquals(MatchingState.FULL_MATCH, aListType.matches(bListType, matches));
	}

	@Test
	public void aListShouldNotBeMatchingB() throws NoSuchMethodException, SecurityException {
		Method aList = A.class.getDeclaredMethod("list");

		ResolvableType aListType = ResolvableType.forMethodReturnType(aList);
		ResolvableType aType = ResolvableType.forClass(A.class);

		assertEquals(MatchingState.NO_MATCH, aListType.matches(aType, matches));
	}

	@Test
	public void aListShouldNotBeMatchingBEvenIfAMatchesB() throws NoSuchMethodException, SecurityException {
		Method aList = A.class.getDeclaredMethod("list");

		ResolvableType aListType = ResolvableType.forMethodReturnType(aList);
		ResolvableType bType = ResolvableType.forClass(B.class);

		matches.put(A.class, B.class);
		assertEquals(MatchingState.NO_MATCH, aListType.matches(bType, matches));
	}

	@Test
	public void aShouldNotBeMatchingListB() throws NoSuchMethodException, SecurityException {
		Method aList = A.class.getDeclaredMethod("list");

		ResolvableType aListType = ResolvableType.forMethodReturnType(aList);
		ResolvableType bType = ResolvableType.forClass(A.class);

		assertEquals(MatchingState.NO_MATCH, bType.matches(aListType, matches));
	}

	@Test
	public void extendsAListShouldBeMatchingExtendsBListIfAMatchesB() throws NoSuchMethodException, SecurityException {
		Method aList = A.class.getDeclaredMethod("extendsList");
		Method bList = B.class.getDeclaredMethod("extendsList");

		ResolvableType aListType = ResolvableType.forMethodReturnType(aList);
		ResolvableType bListType = ResolvableType.forMethodReturnType(bList);

		matches.put(A.class, B.class);
		assertEquals(MatchingState.FULL_MATCH, aListType.matches(bListType, matches));
	}

	@Test
	public void superAListShouldBeMatchingSuperBListIfAMatchesB() throws NoSuchMethodException, SecurityException {
		Method aList = A.class.getDeclaredMethod("superList");
		Method bList = B.class.getDeclaredMethod("superList");

		ResolvableType aListType = ResolvableType.forMethodReturnType(aList);
		ResolvableType bListType = ResolvableType.forMethodReturnType(bList);

		matches.put(A.class, B.class);
		assertEquals(MatchingState.FULL_MATCH, aListType.matches(bListType, matches));
	}

	@Test
	public void aArrayShouldBeMatchingBArrayIfAMatchesB() throws NoSuchMethodException, SecurityException {
		Method aArray = A.class.getDeclaredMethod("array");
		Method bArray = B.class.getDeclaredMethod("array");

		ResolvableType aArrayType = ResolvableType.forMethodReturnType(aArray);
		ResolvableType bArrayType = ResolvableType.forMethodReturnType(bArray);

		matches.put(A.class, B.class);
		assertEquals(MatchingState.FULL_MATCH, aArrayType.matches(bArrayType, matches));
	}

	@Test
	public void typeVariableEShouldBeMatchingItself() throws NoSuchMethodException, SecurityException {
		Method dElement = D.class.getDeclaredMethod("element");

		ResolvableType eType = ResolvableType.forMethodReturnType(dElement);

		assertEquals(MatchingState.FULL_MATCH, eType.matches(eType, matches));
	}

	@Test
	public void typeVariableEListShouldBeMatchingTypeVariableGListIfDMatchesF() throws NoSuchMethodException, SecurityException {
		Method dList = D.class.getDeclaredMethod("list");
		Method fList = F.class.getDeclaredMethod("list");

		ResolvableType dListType = ResolvableType.forMethodReturnType(dList);
		ResolvableType fListType = ResolvableType.forMethodReturnType(fList);

		matches.put(D.class, F.class);
		assertEquals(MatchingState.FULL_MATCH, dListType.matches(fListType, matches));
	}

	@Test
	public void typeVariableEArrayShouldNotMatchTypeVariableGArray() throws NoSuchMethodException, SecurityException {
		Method eArray = D.class.getDeclaredMethod("array");
		Method gArray = F.class.getDeclaredMethod("array");

		ResolvableType eArrayType = ResolvableType.forMethodReturnType(eArray);
		ResolvableType gArrayType = ResolvableType.forMethodReturnType(gArray);

		assertEquals(MatchingState.NO_MATCH, eArrayType.matches(gArrayType, matches));
	}

	@Test
	public void typeVariableEArrayShouldBeMatchingItself() throws NoSuchMethodException, SecurityException {
		Method eArray = D.class.getDeclaredMethod("array");

		ResolvableType eArrayType = ResolvableType.forMethodReturnType(eArray);

		assertEquals(MatchingState.FULL_MATCH, eArrayType.matches(eArrayType, matches));
	}

	@Test
	public void typeVariableEArrayShouldBeMatchingTypeVariableGArrayIfDMatchesF() throws NoSuchMethodException, SecurityException {
		Method eArray = D.class.getDeclaredMethod("array");
		Method gArray = F.class.getDeclaredMethod("array");
		
		ResolvableType eArrayType = ResolvableType.forMethodReturnType(eArray);
		ResolvableType gArrayType = ResolvableType.forMethodReturnType(gArray);
		
		matches.put(D.class, F.class);
		assertEquals(MatchingState.FULL_MATCH, eArrayType.matches(gArrayType, matches));
	}

	@Test
	public void listOfTypeVariableEArrayShouldNotMatchListOfTypeVariableGArray() throws NoSuchMethodException, SecurityException {
		Method eArray = D.class.getDeclaredMethod("listOfArray");
		Method gArray = F.class.getDeclaredMethod("listOfArray");

		ResolvableType eArrayType = ResolvableType.forMethodReturnType(eArray);
		ResolvableType gArrayType = ResolvableType.forMethodReturnType(gArray);

		assertEquals(MatchingState.NO_MATCH, eArrayType.matches(gArrayType, matches));
	}

	@Test
	public void listOfTypeVariableEArrayShouldBeMatchingListOfTypeVariableGArrayIfDMatchesF() throws NoSuchMethodException, SecurityException {
		Method eList = D.class.getDeclaredMethod("listOfArray");
		Method gList = F.class.getDeclaredMethod("listOfArray");
		
		ResolvableType eListType = ResolvableType.forMethodReturnType(eList);
		ResolvableType gListType = ResolvableType.forMethodReturnType(gList);
		
		matches.put(D.class, F.class);
		assertEquals(MatchingState.FULL_MATCH, eListType.matches(gListType, matches));
	}

	@Test
	public void listOfAArrayShouldNotMatchListOfBArray() throws NoSuchMethodException, SecurityException {
		Method aArray = A.class.getDeclaredMethod("listOfArray");
		Method bArray = B.class.getDeclaredMethod("listOfArray");

		ResolvableType aArrayType = ResolvableType.forMethodReturnType(aArray);
		ResolvableType bArrayType = ResolvableType.forMethodReturnType(bArray);

		assertEquals(MatchingState.NO_MATCH, aArrayType.matches(bArrayType, matches));
	}

	@Test
	public void listOfAArrayShouldBeMatchingListOfBArrayIfDMatchesF() throws NoSuchMethodException, SecurityException {
		Method aList = A.class.getDeclaredMethod("listOfArray");
		Method bList = B.class.getDeclaredMethod("listOfArray");
		
		ResolvableType aListType = ResolvableType.forMethodReturnType(aList);
		ResolvableType bListType = ResolvableType.forMethodReturnType(bList);
		
		matches.put(A.class, B.class);
		assertEquals(MatchingState.FULL_MATCH, aListType.matches(bListType, matches));
	}

	public static abstract class A {

		public abstract List<A> list();

		public abstract List<? extends A> extendsList();

		public abstract List<? super A> superList();

		public abstract A[] array();

		public abstract List<A[]> listOfArray();
	}

	public static abstract class B {

		public abstract List<B> list();

		public abstract List<? extends B> extendsList();

		public abstract List<? super B> superList();

		public abstract B[] array();

		public abstract List<B[]> listOfArray();
	}

	public static abstract class D<E> {

		public abstract E element();

		public abstract List<E> list();

		public abstract E[] array();

		public abstract List<E[]> listOfArray();
	}

	public static abstract class F<G> {

		public abstract G element();

		public abstract List<G> list();

		public abstract G[] array();

		public abstract List<G[]> listOfArray();
	}

	public static abstract class ListSupplier {

		public abstract List<String> getStringList();

		public abstract List<? extends String> getExtendsStringList();

		public abstract List<? super String> getSuperStringList();
	}
}
