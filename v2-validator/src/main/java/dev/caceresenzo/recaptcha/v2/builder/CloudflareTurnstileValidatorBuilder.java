package dev.caceresenzo.recaptcha.v2.builder;

import java.net.URI;

import dev.caceresenzo.recaptcha.v2.ReCaptchaV2Validator;
import dev.caceresenzo.recaptcha.v2.client.ReCaptchaV2Client;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;

@Data
@Accessors(fluent = true)
public class CloudflareTurnstileValidatorBuilder implements ReCaptchaV2ValidatorBuilder  {

	public static final URI DEFAULT_VERIFY_URL = URI.create("https://challenges.cloudflare.com/turnstile/v0/siteverify");

	private URI verifyUrl = DEFAULT_VERIFY_URL;
	private String secretKey;

	public CloudflareTurnstileValidatorBuilder alwaysPassesTestSecretKey() {
		return secretKey(TestSecretKeys.ALWAYS_PASSES);
	}

	public CloudflareTurnstileValidatorBuilder alwaysFailsTestSecretKey() {
		return secretKey(TestSecretKeys.ALWAYS_FAILS);
	}

	public CloudflareTurnstileValidatorBuilder yieldsTokenAlreadySpentErrorTestSecretKey() {
		return secretKey(TestSecretKeys.YIELDS_TOKEN_ALREADY_SPENT_ERROR);
	}

	public ReCaptchaV2Validator build() {
		return new ReCaptchaV2Client(secretKey, verifyUrl);
	}

	/** https://developers.cloudflare.com/turnstile/troubleshooting/testing/#dummy-sitekeys-and-secret-keys */
	@UtilityClass
	public static class TestSiteKeys {

		public static final String VISIBLE_ALWAYS_PASSES = "1x00000000000000000000AA";
		public static final String VISIBLE_ALWAYS_BLOCKS = "2x00000000000000000000AB";
		public static final String INVISIBIBLE_ALWAYS_PASSES = "1x00000000000000000000BB";
		public static final String INVISIBIBLE_ALWAYS_BLOCKS = "2x00000000000000000000BB";
		public static final String VISIBLE_FORCES_INTERACTIVE_CHALLENGE = "3x00000000000000000000FF";

	}

	/** https://developers.cloudflare.com/turnstile/troubleshooting/testing/#dummy-sitekeys-and-secret-keys */
	@UtilityClass
	public static class TestSecretKeys {

		public static final String ALWAYS_PASSES = "1x0000000000000000000000000000000AA";
		public static final String ALWAYS_FAILS = "2x0000000000000000000000000000000AA";
		public static final String YIELDS_TOKEN_ALREADY_SPENT_ERROR = "3x0000000000000000000000000000000AA";

	}

}