package dev.caceresenzo.recaptcha.spring.web;

import java.util.Arrays;

import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import dev.caceresenzo.recaptcha.spring.web.annotation.ReCaptchaV2;
import dev.caceresenzo.recaptcha.v2.ReCaptchaV2Response;
import dev.caceresenzo.recaptcha.v2.ReCaptchaV2Validator;
import dev.caceresenzo.recaptcha.v2.client.ReCaptchaV2ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReCaptchaV2AnnotationInterceptor implements HandlerInterceptor {

	public static final String RESPONSE_ATTRIBUTE = "dev.caceresenzo.recaptcha/response";

	private final ReCaptchaV2Validator validator;
	private final ReCaptchaV2ValidatorDefaults defaults;
	private final ResponseHandler responseHandler;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (!(handler instanceof HandlerMethod handlerMethod)) {
			return true;
		}

		final var method = handlerMethod.getMethod();

		final var annotation = method.getAnnotation(ReCaptchaV2.class);
		if (annotation == null) {
			return true;
		}

		final var hasNoResponseParameter = Arrays.stream(handlerMethod.getMethodParameters()).noneMatch(ReCaptchaV2ArgumentResolver::isResponseType);

		final var reCaptchaResponse = processResponse(request, annotation, hasNoResponseParameter);
		request.setAttribute(RESPONSE_ATTRIBUTE, reCaptchaResponse);

		if (hasNoResponseParameter) {
			responseHandler.handle(reCaptchaResponse);
		}

		return true;
	}

	private ReCaptchaV2Response processResponse(HttpServletRequest request, ReCaptchaV2 annotation, boolean hasNoResponseParameter) throws MissingRequestValueException {
		final String challengeResponse;
		try {
			challengeResponse = defaults.resolve(
				annotation.location(),
				annotation.name(),
				request
			);
		} catch (MissingRequestValueException exception) {
			if (hasNoResponseParameter) {
				throw exception;
			}

			return new ReCaptchaV2Response.Failure(false, ReCaptchaV2ErrorCode.Standard.MISSING_INPUT_RESPONSE.message());
		}

		return validator.verify(challengeResponse);
	}

	@FunctionalInterface
	public interface ResponseHandler {

		void handle(ReCaptchaV2Response response);

	}

}