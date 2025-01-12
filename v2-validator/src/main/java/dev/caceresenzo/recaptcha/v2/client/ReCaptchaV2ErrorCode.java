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

		MISSING_INPUT_SECRET(false),
		INVALID_INPUT_SECRET(false),
		MISSING_INPUT_RESPONSE(true),
		INVALID_INPUT_RESPONSE(true),
		INVALID_KEYS(false),
		BAD_REQUEST(false),
		TIMEOUT_OR_DUPLICATE(false);

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