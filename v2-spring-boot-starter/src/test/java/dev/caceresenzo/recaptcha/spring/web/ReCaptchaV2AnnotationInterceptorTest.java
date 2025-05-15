package dev.caceresenzo.recaptcha.spring.web;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.method.HandlerMethod;

import dev.caceresenzo.recaptcha.spring.web.annotation.ChallengeResponseLocation;
import dev.caceresenzo.recaptcha.spring.web.annotation.ReCaptchaV2;
import dev.caceresenzo.recaptcha.v2.ReCaptchaV2Response;
import dev.caceresenzo.recaptcha.v2.ReCaptchaV2Validator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.StandardException;

class ReCaptchaV2AnnotationInterceptorTest {

	static Controller controller;

	static HandlerMethod withAnnotation;
	static HandlerMethod withoutAnnotation;

	@BeforeAll
	static void setUp() {
		controller = new Controller();

		withAnnotation = new HandlerMethod(controller, ReflectionUtils.findMethod(Controller.class, "withAnnotation"));
		withoutAnnotation = new HandlerMethod(controller, ReflectionUtils.findMethod(Controller.class, "withoutAnnotation"));
	}

	@Test
	void missingAnnotation() {
		final var interceptor = new ReCaptchaV2AnnotationInterceptor(null, null, ReCaptchaV2Response::orThrow);

		assertDoesNotThrow(() -> interceptor.preHandle(null, null, withoutAnnotation));
	}

	@Test
	void withAnnotation() {
		final var validator = mock(ReCaptchaV2Validator.class);
		when(validator.verify(anyString())).thenReturn(new ReCaptchaV2Response.Success(LocalDateTime.now(), null));

		final var defaults = new ReCaptchaV2ValidatorDefaults(ChallengeResponseLocation.HEADER);

		final var interceptor = new ReCaptchaV2AnnotationInterceptor(validator, defaults, ReCaptchaV2Response::orThrow);

		assertDoesNotThrow(() -> interceptor.preHandle(null, null, withoutAnnotation));
		verifyNoInteractions(validator);

		final var challengeResponse = "abc";
		final var request = mock(HttpServletRequest.class);
		when(request.getHeader(anyString())).thenReturn(challengeResponse);

		assertDoesNotThrow(() -> interceptor.preHandle(request, null, withAnnotation));
		verify(validator, times(1)).verify(challengeResponse);
	}

	@Test
	void customError() {
		final var errorMessage = "something happen";

		final var validator = mock(ReCaptchaV2Validator.class);
		when(validator.verify(anyString())).thenReturn(new ReCaptchaV2Response.Failure(false, errorMessage));

		final var defaults = new ReCaptchaV2ValidatorDefaults(ChallengeResponseLocation.HEADER);

		final var interceptor = new ReCaptchaV2AnnotationInterceptor(validator, defaults, (response) -> response.orThrowWithMessage(CustomReCaptchaException::new));

		assertDoesNotThrow(() -> interceptor.preHandle(null, null, withoutAnnotation));
		verifyNoInteractions(validator);

		final var challengeResponse = "abc";
		final var request = mock(HttpServletRequest.class);
		when(request.getHeader(anyString())).thenReturn(challengeResponse);

		final var exception = assertThrows(CustomReCaptchaException.class, () -> interceptor.preHandle(request, null, withAnnotation));
		assertEquals(errorMessage, exception.getMessage());
	}

	public static class Controller {

		@ReCaptchaV2
		public void withAnnotation() {}

		public void withoutAnnotation() {}

	}

	@SuppressWarnings("serial")
	@StandardException
	public static class CustomReCaptchaException extends RuntimeException {}

}