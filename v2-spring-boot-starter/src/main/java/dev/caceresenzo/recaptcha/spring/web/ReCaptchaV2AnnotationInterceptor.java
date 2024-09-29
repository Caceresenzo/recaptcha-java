package dev.caceresenzo.recaptcha.spring.web;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import dev.caceresenzo.recaptcha.spring.web.annotation.ReCaptchaV2;
import dev.caceresenzo.recaptcha.v2.ReCaptchaV2Validator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReCaptchaV2AnnotationInterceptor implements HandlerInterceptor {

	private final ReCaptchaV2Validator validator;
	private final ReCaptchaV2ValidatorDefaults defaults;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (!(handler instanceof HandlerMethod handlerMethod)) {
			return true;
		}

		final var method = handlerMethod.getMethod();

		ReCaptchaV2 annotation = method.getAnnotation(ReCaptchaV2.class);
		if (annotation == null) {
			return true;
		}

		final var challengeResponse = defaults.resolve(
			annotation.location(),
			annotation.name(),
			request
		);

		validator.verify(challengeResponse).orThrows();
		return true;
	}

}