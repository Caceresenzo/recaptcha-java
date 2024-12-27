package dev.caceresenzo.recaptcha.spring.boot.autoconfigure;

import org.springdoc.core.service.AbstractRequestService;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.caceresenzo.recaptcha.spring.web.ReCaptchaV2ValidatorDefaults;
import dev.caceresenzo.recaptcha.spring.web.swagger.ReCaptchaV2SwaggerCustomizer;
import dev.caceresenzo.recaptcha.v2.ReCaptchaV2Response;
import dev.caceresenzo.recaptcha.v2.ReCaptchaV2Validator;

@ConditionalOnClass(SpringDocUtils.class)
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(ReCaptchaV2AutoConfiguration.class)
public class ReCaptchaSwaggerAutoConfiguration {

	@Bean
	@ConditionalOnWebApplication
	@ConditionalOnBean(ReCaptchaV2Validator.class)
	ReCaptchaV2SwaggerCustomizer reCaptchaV2SwaggerCustomizer(
		ReCaptchaV2ValidatorDefaults reCaptchaV2ValidatorDefaults
	) {
		AbstractRequestService.addRequestWrapperToIgnore(ReCaptchaV2Response.class);
		AbstractRequestService.addRequestWrapperToIgnore(ReCaptchaV2Response.Success.class);
		AbstractRequestService.addRequestWrapperToIgnore(ReCaptchaV2Response.Error.class);

		return new ReCaptchaV2SwaggerCustomizer(
			reCaptchaV2ValidatorDefaults
		);
	}

}