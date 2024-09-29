package dev.caceresenzo.recaptcha.v2.client;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author https://www.baeldung.com/spring-security-registration-captcha#:~:text=3.3.%20Objectifying%20the%20Validation
 */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReCaptchaV2ApiResponse {

	@JsonProperty("success")
	private boolean success;

	@JsonProperty("challenge_ts")
	private LocalDateTime timestamp;

	@JsonProperty("hostname")
	private String hostname;

	@JsonProperty("error-codes")
	private List<ErrorCode> errorCodes;

	@JsonIgnore
	public boolean hasClientError() {
		if (errorCodes == null) {
			return false;
		}

		return errorCodes.stream()
			.anyMatch(ErrorCode::isClient);
	}

	@JsonIgnore
	public String toErrorPhrase() {
		if (errorCodes == null) {
			return null;
		}

		return errorCodes.stream()
			.map(ErrorCode::toMessage)
			.collect(Collectors.joining(", "));
	}

	@AllArgsConstructor
	@Getter
	public enum ErrorCode {

		@JsonProperty("missing-input-secret")
		MISSING_INPUT_SECRET,

		@JsonProperty("invalid-input-secret")
		INVALID_INPUT_SECRET,

		@JsonProperty("missing-input-response")
		MISSING_INPUT_RESPONSE,

		@JsonProperty("invalid-input-response")
		INVALID_INPUT_RESPONSE,

		@JsonProperty("invalid-keys")
		INVALID_KEYS,

		@JsonProperty("bad-request")
		BAD_REQUEST,

		@JsonProperty("timeout-or-duplicate")
		TIMEOUT_OR_DUPLICATE;

		public boolean isClient() {
			return switch (this) {
				case MISSING_INPUT_RESPONSE, INVALID_INPUT_RESPONSE -> true;
				default -> false;
			};
		}

		public String toMessage() {
			return name()
				.replace("_", " ")
				.toLowerCase();
		}

	}

}