package dev.caceresenzo.recaptcha.v2.client;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@JsonDeserialize(using = ReCaptchaV2ErrorCodeDeserializer.class)
public sealed interface ReCaptchaV2ErrorCode {

	boolean isClient();

	String message();

	@AllArgsConstructor
	@Getter
	@Accessors(fluent = true)
	public enum Standard implements ReCaptchaV2ErrorCode {

		/**
		 * The secret parameter is missing.
		 */
		MISSING_INPUT_SECRET(false),

		/**
		 * 	The secret parameter is invalid or malformed.
		 */
		INVALID_INPUT_SECRET(false),

		/**
		 * The response parameter is missing.
		 */
		MISSING_INPUT_RESPONSE(true),

		/**
		 * The response parameter is invalid or malformed.
		 */
		INVALID_INPUT_RESPONSE(true),

		/**
		 * Undocumented error code that indicates the keys are invalid. <br />
		 * Only for Google ReCaptcha.
		 */
		INVALID_KEYS(false),

		/**
		 * The request is invalid or malformed.
		 */
		BAD_REQUEST(false),

		/**
		 * The response is no longer valid: either is too old or has been used previously.
		 */
		TIMEOUT_OR_DUPLICATE(false),

		/**
		 * An internal error happened while validating the response. The request can be retried. <br />
		 * Only for Cloudflare Turnstile.
		 */
		INTERNAL_ERROR(false);

		private final boolean isClient;
		private final String message;

		private Standard(boolean isClient) {
			this.isClient = isClient;
			this.message = name()
				.replace("_", " ")
				.toLowerCase();
		}

	}

	public record Custom(String message) implements ReCaptchaV2ErrorCode {

		@Override
		public boolean isClient() {
			return false;
		}

	}

}