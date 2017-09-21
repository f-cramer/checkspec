package checkspec.report.output.gui;

/*-
 * #%L
 * CheckSpec Output
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

import java.util.function.Function;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * Result of an export operation.
 *
 * @author Florian Cramer
 *
 * @param <T>
 *            result type
 */
interface ExportResult<T> {

	<U> ExportResult<U> map(Function<? super T, U> mapper);

	<U> ExportResult<U> flatMap(Function<? super T, ExportResult<U>> mapper);

	T orElse(T other);

	boolean isSuccess();

	boolean isError();

	boolean isNoExport();

	static <T> ExportResult<T> of(T value) {
		if (value == null) {
			return new NoExport<>();
		} else {
			return new ExportSuccess<T>(value);
		}
	}

	static <T> ExportResult<T> error() {
		return new ExportError<>();
	}

	static <T> ExportResult<T> noExport() {
		return new NoExport<>();
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	static class ExportSuccess<T> implements ExportResult<T> {

		private final T value;

		@Override
		public <U> ExportResult<U> map(Function<? super T, U> mapper) {
			return ExportResult.of(mapper.apply(value));
		}

		@Override
		public <U> ExportResult<U> flatMap(Function<? super T, ExportResult<U>> mapper) {
			return mapper.apply(value);
		}

		@Override
		public T orElse(T other) {
			return value;
		}

		@Override
		public boolean isSuccess() {
			return true;
		}

		@Override
		public boolean isError() {
			return false;
		}

		@Override
		public boolean isNoExport() {
			return false;
		}

	}

	static class ExportError<T> implements ExportResult<T> {

		@Override
		@SuppressWarnings("unchecked")
		public <U> ExportResult<U> map(Function<? super T, U> mapper) {
			return (ExportResult<U>) this;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <U> ExportResult<U> flatMap(Function<? super T, ExportResult<U>> mapper) {
			return (ExportResult<U>) this;
		}

		@Override
		public T orElse(T other) {
			return other;
		}

		@Override
		public boolean isSuccess() {
			return false;
		}

		@Override
		public boolean isError() {
			return true;
		}

		@Override
		public boolean isNoExport() {
			return false;
		}
	}

	static class NoExport<T> implements ExportResult<T> {

		@Override
		@SuppressWarnings("unchecked")
		public <U> ExportResult<U> map(Function<? super T, U> mapper) {
			return (ExportResult<U>) this;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <U> ExportResult<U> flatMap(Function<? super T, ExportResult<U>> mapper) {
			return (ExportResult<U>) this;
		}

		@Override
		public T orElse(T other) {
			return other;
		}

		@Override
		public boolean isSuccess() {
			return false;
		}

		@Override
		public boolean isError() {
			return false;
		}

		@Override
		public boolean isNoExport() {
			return true;
		}
	}
}
