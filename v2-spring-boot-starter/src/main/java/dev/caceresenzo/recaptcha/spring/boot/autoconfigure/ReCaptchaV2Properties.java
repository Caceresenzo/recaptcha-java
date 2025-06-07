package dev.caceresenzo.recaptcha.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import dev.caceresenzo.recaptcha.spring.web.annotation.ChallengeResponseLocation;
import dev.caceresenzo.recaptcha.spring.web.annotation.ReCaptchaV2;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Validated
@Data
@ConfigurationProperties(prefix = ReCaptchaV2Properties.PREFIX)
public class ReCaptchaV2Properties {

	public static final String PREFIX = "recaptcha.v2";
	public static final String PREFIX_SECRET_KEY = PREFIX + ".secret-key";

	private String secretKey;

	@NotNull
	private Service service = Service.GOOGLE_RECAPTCHA;

	@NotNull
	private WebProperties web = new WebProperties();

	@Data
	public class WebProperties {

		private ChallengeResponseLocation location = ChallengeResponseLocation.QUERY;
		private String headerName = ReCaptchaV2.DEFAULT_HEADER_NAME;
		private String queryParameterName = ReCaptchaV2.DEFAULT_QUERY_PARAMETER_NAME;

	}

	public enum Service {

		GOOGLE_RECAPTCHA,
		CLOUDFLARE_TURNSTILE,

	}

}