package dev.caceresenzo.recaptcha.v2.builder;

import java.net.URI;

import dev.caceresenzo.recaptcha.v2.ReCaptchaV2Validator;
import dev.caceresenzo.recaptcha.v2.client.ReCaptchaV2Client;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;

@Data
@Accessors(fluent = true)
public class GoogleReCaptchaV2ValidatorBuilder implements ReCaptchaV2ValidatorBuilder {

	public static final URI DEFAULT_VERIFY_URL = URI.create("https://www.google.com/recaptcha/api/siteverify");

	private URI verifyUrl = DEFAULT_VERIFY_URL;
	private String secretKey;

	public GoogleReCaptchaV2ValidatorBuilder testSecretKey() {
		return secretKey(TestKeys.SECRET);
	}

	@Override
	public ReCaptchaV2Validator build() {
		return new ReCaptchaV2Client(secretKey, verifyUrl);
	}

	/** https://developers.google.com/recaptcha/docs/faq#id-like-to-run-automated-tests-with-recaptcha.-what-should-i-do */
	@UtilityClass
	public static class TestKeys {

		public static final String SITE = "6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI";
		public static final String SECRET = "6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe";

	}

}