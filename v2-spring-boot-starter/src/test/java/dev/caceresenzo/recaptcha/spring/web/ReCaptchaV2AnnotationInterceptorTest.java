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
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.method.HandlerMethod;

import dev.caceresenzo.recaptcha.spring.web.ReCaptchaV2ArgumentResolver.ExpectResponseParameterResult;
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
	static HandlerMethod expectNone;
	static HandlerMethod expectAny;
	static HandlerMethod expectSuccess;
	static HandlerMethod expectFailure;

	@BeforeAll
	static void setUp() {
		controller = new Controller();

		withAnnotation = new HandlerMethod(controller, ReflectionUtils.findMethod(Controller.class, "withAnnotation"));
		withoutAnnotation = new HandlerMethod(controller, ReflectionUtils.findMethod(Controller.class, "withoutAnnotation"));
		expectNone = new HandlerMethod(controller, ReflectionUtils.findMethod(Controller.class, "expectNone"));
		expectAny = new HandlerMethod(controller, ReflectionUtils.findMethod(Controller.class, "expectAny", ReCaptchaV2Response.class));
		expectSuccess = new HandlerMethod(controller, ReflectionUtils.findMethod(Controller.class, "expectSuccess", ReCaptchaV2Response.Success.class));
		expectFailure = new HandlerMethod(controller, ReflectionUtils.findMethod(Controller.class, "expectFailure", ReCaptchaV2Response.Failure.class));
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

	/** @see {@link ExpectResponseParameterResult#NONE}. */
	@Test
	void expectNone() {
		assertThrows(MissingRequestValueException.class, () -> expectTest(expectNone));
	}

	/** @see {@link ExpectResponseParameterResult#ANY}. */
	@Test
	void expectAny() {
		assertDoesNotThrow(() -> expectTest(expectAny));
	}

	/** @see {@link ExpectResponseParameterResult#SUCCESS}. */
	@Test
	void expectSuccess() {
		assertThrows(MissingRequestValueException.class, () -> expectTest(expectSuccess));
	}

	/** @see {@link ExpectResponseParameterResult#FAILURE}. */
	@Test
	void expectFailure() {
		assertDoesNotThrow(() -> expectTest(expectFailure));
	}

	void expectTest(HandlerMethod method) throws MissingRequestValueException {
		final var defaults = new ReCaptchaV2ValidatorDefaults(ChallengeResponseLocation.HEADER);
		final var interceptor = new ReCaptchaV2AnnotationInterceptor(null, defaults, ReCaptchaV2Response::orThrow);

		final var request = mock(HttpServletRequest.class);

		interceptor.preHandle(request, null, method);
	}

	public static class Controller {

		@ReCaptchaV2
		public void withAnnotation() {}

		public void withoutAnnotation() {}

		@ReCaptchaV2
		public void expectNone() {}

		@ReCaptchaV2
		public void expectAny(ReCaptchaV2Response reCaptchaResponse) {}

		@ReCaptchaV2
		public void expectSuccess(ReCaptchaV2Response.Success reCaptchaResponse) {}

		@ReCaptchaV2
		public void expectFailure(ReCaptchaV2Response.Failure reCaptchaResponse) {}

	}

	@SuppressWarnings("serial")
	@StandardException
	public static class CustomReCaptchaException extends RuntimeException {}

}