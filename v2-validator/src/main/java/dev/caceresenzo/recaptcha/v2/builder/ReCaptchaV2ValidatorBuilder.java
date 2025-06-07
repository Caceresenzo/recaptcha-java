package dev.caceresenzo.recaptcha.v2.builder;

import java.net.URI;

import dev.caceresenzo.recaptcha.v2.ReCaptchaV2Validator;

public interface ReCaptchaV2ValidatorBuilder {

	ReCaptchaV2ValidatorBuilder verifyUrl(URI verifyUrl);

	ReCaptchaV2ValidatorBuilder secretKey(String secretKey);

	ReCaptchaV2Validator build();

}