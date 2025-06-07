package dev.caceresenzo.recaptcha.v2;

import java.net.InetAddress;

import dev.caceresenzo.recaptcha.v2.builder.CloudflareTurnstileValidatorBuilder;
import dev.caceresenzo.recaptcha.v2.builder.GoogleReCaptchaV2ValidatorBuilder;

public interface ReCaptchaV2Validator {

	ReCaptchaV2Response verify(String challengeResponse);

	ReCaptchaV2Response verify(String challengeResponse, String remoteIp);

	default ReCaptchaV2Response verify(String challengeResponse, InetAddress remoteIp) {
		return verify(challengeResponse, remoteIp.toString());
	}

	public static GoogleReCaptchaV2ValidatorBuilder builder() {
		return new GoogleReCaptchaV2ValidatorBuilder();
	}

	public static CloudflareTurnstileValidatorBuilder turnstile() {
		return new CloudflareTurnstileValidatorBuilder();
	}

}