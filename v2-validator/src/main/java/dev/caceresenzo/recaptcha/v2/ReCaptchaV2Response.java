package dev.caceresenzo.recaptcha.v2;

import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.function.Supplier;

import dev.caceresenzo.recaptcha.ReCaptchaException;

public sealed interface ReCaptchaV2Response {

	default void orThrow() {
		if (this instanceof Error error) {
			throw new ReCaptchaException(error.message());
		}
	}

	default <X extends Throwable> Success orThrow(Supplier<? extends X> exceptionSupplier) throws X {
		if (this instanceof Success success) {
			return success;
		} else {
			throw exceptionSupplier.get();
		}
	}

	default <X extends Throwable> Success orThrowWithError(Function<Error, ? extends X> exceptionSupplier) throws X {
		if (this instanceof Success success) {
			return success;
		} else {
			throw exceptionSupplier.apply((Error) this);
		}
	}

	default <X extends Throwable> Success orThrowWithMessage(Function<String, ? extends X> exceptionSupplier) throws X {
		if (this instanceof Success success) {
			return success;
		} else {
			throw exceptionSupplier.apply(((Error) this).message());
		}
	}

	public static record Success(
		LocalDateTime challengeTimestamp,
		String hostname
	) implements ReCaptchaV2Response {}

	public static record Error(
		boolean isFromClient,
		String message
	) implements ReCaptchaV2Response {}

}