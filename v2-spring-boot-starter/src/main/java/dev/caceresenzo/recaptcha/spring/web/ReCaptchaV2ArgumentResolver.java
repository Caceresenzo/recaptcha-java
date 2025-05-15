package dev.caceresenzo.recaptcha.spring.web;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import dev.caceresenzo.recaptcha.v2.ReCaptchaV2Response;

public class ReCaptchaV2ArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return !ExpectResponseParameterResult.NONE.equals(isResponseType(parameter));
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		final var response = webRequest.getAttribute(ReCaptchaV2AnnotationInterceptor.RESPONSE_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
		if (response == null) {
			return null;
		}

		if (
			ReCaptchaV2Response.class.equals(parameter.getParameterType()) ||
			response.getClass().equals(parameter.getParameterType())
		) {
			return response;
		}

		return null;
	}

	public static ExpectResponseParameterResult isResponseType(MethodParameter parameter) {
		final var type = parameter.getParameterType();

		if (type == ReCaptchaV2Response.class) {
			return ExpectResponseParameterResult.ANY;
		}

		if (type == ReCaptchaV2Response.Success.class) {
			return ExpectResponseParameterResult.SUCCESS;
		}

		if (type == ReCaptchaV2Response.Failure.class) {
			return ExpectResponseParameterResult.FAILURE;
		}

		return ExpectResponseParameterResult.NONE;
	}

	public enum ExpectResponseParameterResult {

		/**
		 * No response parameter has been found. <br />
		 * Success is assumed by default, throwing is required.
		 */
		NONE,

		/**
		 * The interface {@link ReCaptchaV2Response} has been found. <br />
		 * Result must be handled by the user.
		 */
		ANY,

		/**
		 * The interface {@link ReCaptchaV2Response.Success} has been found. <br />
		 * Success is forced, throwing is required.
		 */
		SUCCESS,

		/**
		 * The interface {@link ReCaptchaV2Response.Failure} has been found. <br />
		 * Failure is expected, result must be handled by the user.
		 */
		FAILURE;

		public boolean canThrowDirectly() {
			return switch (this) {
				case NONE, SUCCESS -> true;
				case ANY, FAILURE -> false;
			};
		}

	}

}