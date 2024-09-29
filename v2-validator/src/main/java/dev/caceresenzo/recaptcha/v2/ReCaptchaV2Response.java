package dev.caceresenzo.recaptcha.v2;

import java.time.LocalDateTime;

import dev.caceresenzo.recaptcha.ReCaptchaException;

public sealed interface ReCaptchaV2Response {

	void orThrows();

	public static record Success(
		LocalDateTime challengeTimestamp,
		String hostname
	) implements ReCaptchaV2Response {

		@Override
		public void orThrows() {}

	}

	public static record Error(
		boolean isFromClient,
		String message
	) implements ReCaptchaV2Response {

		@Override
		public void orThrows() {
			throw new ReCaptchaException(message);
		}

	}

}