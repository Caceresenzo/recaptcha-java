package dev.caceresenzo.recaptcha.spring.boot.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import dev.caceresenzo.recaptcha.spring.web.ReCaptchaV2AnnotationInterceptor;
import dev.caceresenzo.recaptcha.spring.web.ReCaptchaV2ValidatorDefaults;
import dev.caceresenzo.recaptcha.v2.ReCaptchaV2Validator;

@Configuration(proxyBeanMethods = false)
public class ReCaptchaAnnotationWebAutoConfiguration implements WebMvcConfigurer {

	@Lazy
	@Autowired(required = false)
	ReCaptchaV2AnnotationInterceptor reCaptchaV2AnnotationInterceptor;

	@Bean
	@ConditionalOnWebApplication
	@ConditionalOnBean(ReCaptchaV2Validator.class)
	ReCaptchaV2AnnotationInterceptor reCaptchaV2AnnotationInterceptor(
		ReCaptchaV2Validator reCaptchaV2Validator,
		ReCaptchaV2ValidatorDefaults reCaptchaV2ValidatorDefaults
	) {
		return new ReCaptchaV2AnnotationInterceptor(
			reCaptchaV2Validator,
			reCaptchaV2ValidatorDefaults
		);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		if (reCaptchaV2AnnotationInterceptor != null) {
			registry.addInterceptor(reCaptchaV2AnnotationInterceptor);
		}
	}

}