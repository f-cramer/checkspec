package checkspec.report.output.gui;

import java.util.function.Function;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

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