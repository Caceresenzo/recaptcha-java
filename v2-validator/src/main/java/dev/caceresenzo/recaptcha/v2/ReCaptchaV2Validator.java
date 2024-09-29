package dev.caceresenzo.recaptcha.v2;

import java.net.InetAddress;

import dev.caceresenzo.recaptcha.v2.client.ReCaptchaV2Client;
import lombok.Data;
import lombok.experimental.Accessors;

public interface ReCaptchaV2Validator {

	/** https://developers.google.com/recaptcha/docs/faq#id-like-to-run-automated-tests-with-recaptcha.-what-should-i-do */
	public static final String TEST_SITE_KEY = "6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI";

	/** https://developers.google.com/recaptcha/docs/faq#id-like-to-run-automated-tests-with-recaptcha.-what-should-i-do */
	public static final String TEST_SECRET_KEY = "6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe";

	ReCaptchaV2Response verify(String challengeResponse);

	ReCaptchaV2Response verify(String challengeResponse, String remoteIp);

	default ReCaptchaV2Response verify(String challengeResponse, InetAddress remoteIp) {
		return verify(challengeResponse, remoteIp.toString());
	}

	public static Builder builder() {
		return new Builder();
	}

	@Data
	@Accessors(fluent = true)
	public static class Builder {

		private String secretKey;

		public Builder testSecretKey() {
			return secretKey(TEST_SECRET_KEY);
		}

		public ReCaptchaV2Validator build() {
			return new ReCaptchaV2Client(secretKey);
		}

	}

}