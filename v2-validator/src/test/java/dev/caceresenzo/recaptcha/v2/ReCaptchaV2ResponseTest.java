package dev.caceresenzo.recaptcha.v2;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import dev.caceresenzo.recaptcha.ReCaptchaException;

class ReCaptchaV2ResponseTest {

	static final ReCaptchaV2Response.Success SUCCESS = new ReCaptchaV2Response.Success(null, null);
	static final ReCaptchaV2Response.Failure ERROR = new ReCaptchaV2Response.Failure(true, "hello");

	@Test
	void orThrow() {
		assertDoesNotThrow(() -> SUCCESS.orThrow());

		assertThrows(ReCaptchaException.class, () -> ERROR.orThrow());
	}

	@Test
	void orThrow_supplier() {
		assertDoesNotThrow(() -> SUCCESS.orThrow(IllegalStateException::new));

		final var exception = assertThrows(IllegalStateException.class, () -> ERROR.orThrow(IllegalStateException::new));
		assertNull(exception.getMessage());
	}

	@Test
	void orThrowWithError() {
		assertDoesNotThrow(() -> SUCCESS.orThrowWithFailure((error) -> new IllegalStateException(error.toString())));

		final var exception = assertThrows(IllegalStateException.class, () -> ERROR.orThrowWithFailure((error) -> new IllegalStateException(error.toString())));
		assertEquals(ERROR.toString(), exception.getMessage());
	}

	@Test
	void orThrowWithMessage() {
		assertDoesNotThrow(() -> SUCCESS.orThrowWithMessage(IllegalStateException::new));

		final var exception = assertThrows(IllegalStateException.class, () -> ERROR.orThrowWithMessage(IllegalStateException::new));
		assertEquals(ERROR.message(), exception.getMessage());
	}

}