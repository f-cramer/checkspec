package checkspec.type;

/*-
 * #%L
 * CheckSpec Commons
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



import static org.assertj.core.api.Assertions.*;

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

	private MatchableType stringType;
	private MatchableType listType;
	private MultiValuedMap<Class<?>, Class<?>> matches;

	@Before
	public void setUp() throws Exception {
		stringType = MatchableType.forClass(stringClass);
		listType = MatchableType.forClass(listClass);
		matches = new HashSetValuedHashMap<>();
	}

	@Test
	public void intShouldPartiallyMatchInteger() {
		MatchableType intType = MatchableType.forClass(int.class);
		MatchableType integerType = MatchableType.forClass(Integer.class);
		assertThat(intType.matches(integerType, matches)).isEqualTo(MatchingState.PARTIAL_MATCH);
	}

	@Test
	public void listStringShouldPartiallyMatchListExtendsString() throws NoSuchMethodException, SecurityException {
		Method getStringList = ListSupplier.class.getDeclaredMethod("getStringList");
		MatchableType stringListType = MatchableType.forMethodReturnType(getStringList);
		Method getExtendsStringList = ListSupplier.class.getDeclaredMethod("getExtendsStringList");
		MatchableType extendsStringListType = MatchableType.forMethodReturnType(getExtendsStringList);

		assertThat(stringListType.matches(extendsStringListType, matches)).isEqualTo(MatchingState.PARTIAL_MATCH);
	}

	@Test
	public void listStringShouldPartiallyMatchListSuperString() throws NoSuchMethodException, SecurityException {
		Method getStringList = ListSupplier.class.getDeclaredMethod("getStringList");
		MatchableType stringListType = MatchableType.forMethodReturnType(getStringList);
		Method getSuperStringList = ListSupplier.class.getDeclaredMethod("getSuperStringList");
		MatchableType superStringListType = MatchableType.forMethodReturnType(getSuperStringList);

		assertThat(stringListType.matches(superStringListType, matches)).isEqualTo(MatchingState.PARTIAL_MATCH);
	}

	@Test
	public void listExtendsStringShouldPartiallyMatchListString() throws NoSuchMethodException, SecurityException {
		Method getExtendsStringList = ListSupplier.class.getDeclaredMethod("getExtendsStringList");
		MatchableType extendsStringListType = MatchableType.forMethodReturnType(getExtendsStringList);
		Method getStringList = ListSupplier.class.getDeclaredMethod("getStringList");
		MatchableType stringListType = MatchableType.forMethodReturnType(getStringList);

		assertThat(extendsStringListType.matches(stringListType, matches)).isEqualTo(MatchingState.PARTIAL_MATCH);
	}

	@Test
	public void listSuperStringShouldPartiallyMatchListString() throws NoSuchMethodException, SecurityException {
		Method getSuperStringList = ListSupplier.class.getDeclaredMethod("getSuperStringList");
		MatchableType superStringListType = MatchableType.forMethodReturnType(getSuperStringList);
		Method getStringList = ListSupplier.class.getDeclaredMethod("getStringList");
		MatchableType stringListType = MatchableType.forMethodReturnType(getStringList);

		assertThat(superStringListType.matches(stringListType, matches)).isEqualTo(MatchingState.PARTIAL_MATCH);
	}

	@Test
	public void listStringShouldMatchItself() throws NoSuchMethodException, SecurityException {
		Method getStringList = ListSupplier.class.getDeclaredMethod("getStringList");
		MatchableType stringListType = MatchableType.forMethodReturnType(getStringList);

		assertThat(stringListType.matches(stringListType, matches)).isEqualTo(MatchingState.FULL_MATCH);
	}

	@Test
	public void stringShouldMatchItself() {
		assertThat(stringType.matches(stringType, matches)).isEqualTo(MatchingState.FULL_MATCH);
	}

	@Test
	public void stringShouldNotMatchList() {
		assertThat(stringType.matches(listType, matches)).isEqualTo(MatchingState.NO_MATCH);
	}

	@Test
	public void stringShouldMatchListIfStringListPairInMatches() {
		matches.put(stringClass, listClass);
		assertThat(stringType.matches(listType, matches)).isEqualTo(MatchingState.FULL_MATCH);
	}

	@Test
	public void listShouldMatchItself() {
		assertThat(listType.matches(listType, matches)).isEqualTo(MatchingState.FULL_MATCH);
	}

	@Test
	public void listShouldNotMatchString() {
		assertThat(listType.matches(stringType, matches)).isEqualTo(MatchingState.NO_MATCH);
	}

	@Test
	public void listShouldMatchStringIfListStringPairInMatches() {
		matches.put(listClass, stringClass);
		assertThat(listType.matches(stringType, matches)).isEqualTo(MatchingState.FULL_MATCH);
	}

	@Test
	public void aListShouldBeMatchingBListIfAMatchesB() throws NoSuchMethodException, SecurityException {
		Method aList = A.class.getDeclaredMethod("list");
		Method bList = B.class.getDeclaredMethod("list");

		MatchableType aListType = MatchableType.forMethodReturnType(aList);
		MatchableType bListType = MatchableType.forMethodReturnType(bList);

		matches.put(A.class, B.class);
		assertThat(aListType.matches(bListType, matches)).isEqualTo(MatchingState.FULL_MATCH);
	}

	@Test
	public void aListShouldNotBeMatchingB() throws NoSuchMethodException, SecurityException {
		Method aList = A.class.getDeclaredMethod("list");

		MatchableType aListType = MatchableType.forMethodReturnType(aList);
		MatchableType aType = MatchableType.forClass(A.class);

		assertThat(aListType.matches(aType, matches)).isEqualTo(MatchingState.NO_MATCH);
	}

	@Test
	public void aListShouldNotBeMatchingBEvenIfAMatchesB() throws NoSuchMethodException, SecurityException {
		Method aList = A.class.getDeclaredMethod("list");

		MatchableType aListType = MatchableType.forMethodReturnType(aList);
		MatchableType bType = MatchableType.forClass(B.class);

		matches.put(A.class, B.class);
		assertThat(aListType.matches(bType, matches)).isEqualTo(MatchingState.NO_MATCH);
	}

	@Test
	public void aShouldNotBeMatchingListB() throws NoSuchMethodException, SecurityException {
		Method aList = A.class.getDeclaredMethod("list");

		MatchableType aListType = MatchableType.forMethodReturnType(aList);
		MatchableType bType = MatchableType.forClass(A.class);

		assertThat(bType.matches(aListType, matches)).isEqualTo(MatchingState.NO_MATCH);
	}

	@Test
	public void extendsAListShouldBeMatchingExtendsBListIfAMatchesB() throws NoSuchMethodException, SecurityException {
		Method aList = A.class.getDeclaredMethod("extendsList");
		Method bList = B.class.getDeclaredMethod("extendsList");

		MatchableType aListType = MatchableType.forMethodReturnType(aList);
		MatchableType bListType = MatchableType.forMethodReturnType(bList);

		matches.put(A.class, B.class);
		assertThat(aListType.matches(bListType, matches)).isEqualTo(MatchingState.FULL_MATCH);
	}

	@Test
	public void superAListShouldBeMatchingSuperBListIfAMatchesB() throws NoSuchMethodException, SecurityException {
		Method aList = A.class.getDeclaredMethod("superList");
		Method bList = B.class.getDeclaredMethod("superList");

		MatchableType aListType = MatchableType.forMethodReturnType(aList);
		MatchableType bListType = MatchableType.forMethodReturnType(bList);

		matches.put(A.class, B.class);
		assertThat(aListType.matches(bListType, matches)).isEqualTo(MatchingState.FULL_MATCH);
	}

	@Test
	public void aArrayShouldBeMatchingBArrayIfAMatchesB() throws NoSuchMethodException, SecurityException {
		Method aArray = A.class.getDeclaredMethod("array");
		Method bArray = B.class.getDeclaredMethod("array");

		MatchableType aArrayType = MatchableType.forMethodReturnType(aArray);
		MatchableType bArrayType = MatchableType.forMethodReturnType(bArray);

		matches.put(A.class, B.class);
		assertThat(aArrayType.matches(bArrayType, matches)).isEqualTo(MatchingState.FULL_MATCH);
	}

	@Test
	public void typeVariableEShouldBeMatchingItself() throws NoSuchMethodException, SecurityException {
		Method dElement = D.class.getDeclaredMethod("element");

		MatchableType eType = MatchableType.forMethodReturnType(dElement);

		assertThat(eType.matches(eType, matches)).isEqualTo(MatchingState.FULL_MATCH);
	}

	@Test
	public void typeVariableEListShouldBeMatchingTypeVariableGListIfDMatchesF() throws NoSuchMethodException, SecurityException {
		Method dList = D.class.getDeclaredMethod("list");
		Method fList = F.class.getDeclaredMethod("list");

		MatchableType dListType = MatchableType.forMethodReturnType(dList);
		MatchableType fListType = MatchableType.forMethodReturnType(fList);

		matches.put(D.class, F.class);
		assertThat(dListType.matches(fListType, matches)).isEqualTo(MatchingState.FULL_MATCH);
	}

	@Test
	public void typeVariableEArrayShouldNotMatchTypeVariableGArray() throws NoSuchMethodException, SecurityException {
		Method eArray = D.class.getDeclaredMethod("array");
		Method gArray = F.class.getDeclaredMethod("array");

		MatchableType eArrayType = MatchableType.forMethodReturnType(eArray);
		MatchableType gArrayType = MatchableType.forMethodReturnType(gArray);

		assertThat(eArrayType.matches(gArrayType, matches)).isEqualTo(MatchingState.NO_MATCH);
	}

	@Test
	public void typeVariableEArrayShouldBeMatchingItself() throws NoSuchMethodException, SecurityException {
		Method eArray = D.class.getDeclaredMethod("array");

		MatchableType eArrayType = MatchableType.forMethodReturnType(eArray);

		assertThat(eArrayType.matches(eArrayType, matches)).isEqualTo(MatchingState.FULL_MATCH);
	}

	@Test
	public void typeVariableEArrayShouldBeMatchingTypeVariableGArrayIfDMatchesF() throws NoSuchMethodException, SecurityException {
		Method eArray = D.class.getDeclaredMethod("array");
		Method gArray = F.class.getDeclaredMethod("array");

		MatchableType eArrayType = MatchableType.forMethodReturnType(eArray);
		MatchableType gArrayType = MatchableType.forMethodReturnType(gArray);

		matches.put(D.class, F.class);
		assertThat(eArrayType.matches(gArrayType, matches)).isEqualTo(MatchingState.FULL_MATCH);
	}

	@Test
	public void typeVariableEArrayShouldNotMatchString() throws NoSuchMethodException, SecurityException {
		Method eArray = D.class.getDeclaredMethod("array");

		MatchableType eArrayType = MatchableType.forMethodReturnType(eArray);

		matches.put(D.class, F.class);
		assertThat(eArrayType.matches(stringType, matches)).isEqualTo(MatchingState.NO_MATCH);
	}

	@Test
	public void listOfTypeVariableEArrayShouldNotMatchListOfTypeVariableGArray() throws NoSuchMethodException, SecurityException {
		Method eArray = D.class.getDeclaredMethod("listOfArray");
		Method gArray = F.class.getDeclaredMethod("listOfArray");

		MatchableType eArrayType = MatchableType.forMethodReturnType(eArray);
		MatchableType gArrayType = MatchableType.forMethodReturnType(gArray);

		assertThat(eArrayType.matches(gArrayType, matches)).isEqualTo(MatchingState.NO_MATCH);
	}

	@Test
	public void listOfTypeVariableEArrayShouldBeMatchingListOfTypeVariableGArrayIfDMatchesF() throws NoSuchMethodException, SecurityException {
		Method eList = D.class.getDeclaredMethod("listOfArray");
		Method gList = F.class.getDeclaredMethod("listOfArray");

		MatchableType eListType = MatchableType.forMethodReturnType(eList);
		MatchableType gListType = MatchableType.forMethodReturnType(gList);

		matches.put(D.class, F.class);
		assertThat(eListType.matches(gListType, matches)).isEqualTo(MatchingState.FULL_MATCH);
	}

	@Test
	public void listOfAArrayShouldNotMatchListOfBArray() throws NoSuchMethodException, SecurityException {
		Method aArray = A.class.getDeclaredMethod("listOfArray");
		Method bArray = B.class.getDeclaredMethod("listOfArray");

		MatchableType aArrayType = MatchableType.forMethodReturnType(aArray);
		MatchableType bArrayType = MatchableType.forMethodReturnType(bArray);

		assertThat(aArrayType.matches(bArrayType, matches)).isEqualTo(MatchingState.NO_MATCH);
	}

	@Test
	public void listOfAArrayShouldBeMatchingListOfBArrayIfDMatchesF() throws NoSuchMethodException, SecurityException {
		Method aList = A.class.getDeclaredMethod("listOfArray");
		Method bList = B.class.getDeclaredMethod("listOfArray");

		MatchableType aListType = MatchableType.forMethodReturnType(aList);
		MatchableType bListType = MatchableType.forMethodReturnType(bList);

		matches.put(A.class, B.class);
		assertThat(aListType.matches(bListType, matches)).isEqualTo(MatchingState.FULL_MATCH);
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
