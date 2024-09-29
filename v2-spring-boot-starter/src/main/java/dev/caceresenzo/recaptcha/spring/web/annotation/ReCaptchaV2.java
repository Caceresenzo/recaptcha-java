package dev.caceresenzo.recaptcha.spring.web.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface ReCaptchaV2 {

	public static String DEFAULT_HEADER_NAME = "X-ReCaptcha-Response";
	public static String DEFAULT_QUERY_PARAMETER_NAME = "reCaptchaResponse";

	ChallengeResponseLocation location() default ChallengeResponseLocation.DEFAULT;

	String name() default "";

}