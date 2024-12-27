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
		return isResponseType(parameter);
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

	public static boolean isResponseType(MethodParameter parameter) {
		return parameter.getParameterType().isAssignableFrom(ReCaptchaV2Response.class);
	}

}