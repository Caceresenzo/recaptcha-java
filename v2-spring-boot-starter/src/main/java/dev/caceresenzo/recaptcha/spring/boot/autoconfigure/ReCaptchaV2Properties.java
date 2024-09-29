package dev.caceresenzo.recaptcha.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import dev.caceresenzo.recaptcha.spring.web.annotation.ChallengeResponseLocation;
import dev.caceresenzo.recaptcha.spring.web.annotation.ReCaptchaV2;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = ReCaptchaV2Properties.PREFIX)
public class ReCaptchaV2Properties {

	public static final String PREFIX = "recaptcha.v2";
	public static final String PREFIX_SECRET_KEY = PREFIX + ".secret-key";

	private String secretKey;

	private WebProperties web = new WebProperties();

	@Data
	public class WebProperties {

		private ChallengeResponseLocation location = ChallengeResponseLocation.QUERY;
		private String header = ReCaptchaV2.DEFAULT_HEADER;
		private String queryParameter = ReCaptchaV2.DEFAULT_QUERY_PARAMETER;

	}

}