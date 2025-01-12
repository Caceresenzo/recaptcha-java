package dev.caceresenzo.recaptcha.v2.client;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
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
	private List<ReCaptchaV2ErrorCode> errorCodes;

	@JsonIgnore
	public boolean hasClientError() {
		if (errorCodes == null) {
			return false;
		}

		return errorCodes.stream()
			.anyMatch(ReCaptchaV2ErrorCode::isClient);
	}

	@JsonIgnore
	public String toErrorPhrase() {
		if (errorCodes == null) {
			return null;
		}

		return errorCodes.stream()
			.map(ReCaptchaV2ErrorCode::message)
			.collect(Collectors.joining(", "));
	}

}