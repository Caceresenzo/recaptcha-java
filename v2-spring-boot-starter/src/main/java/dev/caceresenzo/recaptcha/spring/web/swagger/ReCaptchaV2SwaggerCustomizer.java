package dev.caceresenzo.recaptcha.spring.web.swagger;

import org.springdoc.core.customizers.GlobalOperationCustomizer;
import org.springframework.web.method.HandlerMethod;

import dev.caceresenzo.recaptcha.spring.web.ReCaptchaV2ValidatorDefaults;
import dev.caceresenzo.recaptcha.spring.web.annotation.ChallengeResponseLocation;
import dev.caceresenzo.recaptcha.spring.web.annotation.ReCaptchaV2;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReCaptchaV2SwaggerCustomizer implements GlobalOperationCustomizer {

	private final ReCaptchaV2ValidatorDefaults defaults;

	@Override
	public Operation customize(Operation operation, HandlerMethod handlerMethod) {
		final var annotation = handlerMethod.getMethodAnnotation(ReCaptchaV2.class);
		if (annotation != null) {
			apply(operation, annotation);
		}

		return operation;
	}

	public void apply(Operation operation, ReCaptchaV2 annotation) {
		final var location = defaults.resolveLocation(annotation.location());
		final var name = defaults.resolveName(location, annotation.name());

		final var parameter = new Parameter()
			.in(ChallengeResponseLocation.HEADER.equals(location) ? "header" : "query")
			.name(name)
			.schema(new StringSchema());

		operation.addParametersItem(parameter);
	}

}