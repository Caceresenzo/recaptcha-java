package dev.caceresenzo.recaptcha.v2;

import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.function.Supplier;

import dev.caceresenzo.recaptcha.ReCaptchaException;

public sealed interface ReCaptchaV2Response {

	void orThrow();

	<X extends Throwable> Success orThrow(Supplier<? extends X> exceptionSupplier) throws X;

	<X extends Throwable> Success orThrowWithFailure(Function<Failure, ? extends X> exceptionSupplier) throws X;

	<X extends Throwable> Success orThrowWithMessage(Function<String, ? extends X> exceptionSupplier) throws X;

	public static record Success(
		LocalDateTime challengeTimestamp,
		String hostname
	) implements ReCaptchaV2Response {

		@Override
		public void orThrow() {
			/* no operation */
		}

		@Override
		public <X extends Throwable> Success orThrow(Supplier<? extends X> exceptionSupplier) throws X {
			return this;
		}

		@Override
		public <X extends Throwable> Success orThrowWithFailure(Function<Failure, ? extends X> exceptionSupplier) throws X {
			return this;
		}

		@Override
		public <X extends Throwable> Success orThrowWithMessage(Function<String, ? extends X> exceptionSupplier) throws X {
			return this;
		}

	}

	public static record Failure(
		boolean isFromClient,
		String message
	) implements ReCaptchaV2Response {

		@Override
		public void orThrow() {
			orThrowWithMessage(ReCaptchaException::new);
		}

		@Override
		public <X extends Throwable> Success orThrow(Supplier<? extends X> exceptionSupplier) throws X {
			throw exceptionSupplier.get();
		}

		public <X extends Throwable> Success orThrowWithFailure(Function<Failure, ? extends X> exceptionSupplier) throws X {
			throw exceptionSupplier.apply(this);
		}

		public <X extends Throwable> Success orThrowWithMessage(Function<String, ? extends X> exceptionSupplier) throws X {
			throw exceptionSupplier.apply(this.message);
		}

	}

}