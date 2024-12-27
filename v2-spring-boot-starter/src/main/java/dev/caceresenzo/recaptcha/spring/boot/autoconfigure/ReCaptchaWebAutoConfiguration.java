package dev.caceresenzo.recaptcha.spring.boot.autoconfigure;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import dev.caceresenzo.recaptcha.spring.web.ReCaptchaV2AnnotationInterceptor;
import dev.caceresenzo.recaptcha.spring.web.ReCaptchaV2ArgumentResolver;
import dev.caceresenzo.recaptcha.spring.web.ReCaptchaV2ValidatorDefaults;
import dev.caceresenzo.recaptcha.v2.ReCaptchaV2Response;
import dev.caceresenzo.recaptcha.v2.ReCaptchaV2Validator;

@Configuration(proxyBeanMethods = false)
public class ReCaptchaWebAutoConfiguration implements WebMvcConfigurer {

	@Lazy
	@Autowired(required = false)
	ReCaptchaV2AnnotationInterceptor reCaptchaV2AnnotationInterceptor;

	@Lazy
	@Autowired(required = false)
	ReCaptchaV2ArgumentResolver reCaptchaV2ArgumentResolver;

	@Bean
	@ConditionalOnWebApplication
	@ConditionalOnBean(ReCaptchaV2Validator.class)
	ReCaptchaV2AnnotationInterceptor reCaptchaV2AnnotationInterceptor(
		ReCaptchaV2Validator reCaptchaV2Validator,
		ReCaptchaV2ValidatorDefaults reCaptchaV2ValidatorDefaults,
		Optional<ReCaptchaV2AnnotationInterceptor.ResponseHandler> reCaptchaV2ResponseHandler
	) {
		return new ReCaptchaV2AnnotationInterceptor(
			reCaptchaV2Validator,
			reCaptchaV2ValidatorDefaults,
			reCaptchaV2ResponseHandler.orElse(ReCaptchaV2Response::orThrow)
		);
	}

	@Bean
	@ConditionalOnWebApplication
	@ConditionalOnBean(ReCaptchaV2Validator.class)
	ReCaptchaV2ArgumentResolver reCaptchaV2ArgumentResolver() {
		return new ReCaptchaV2ArgumentResolver();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		if (reCaptchaV2AnnotationInterceptor != null) {
			registry.addInterceptor(reCaptchaV2AnnotationInterceptor);
		}
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		if (reCaptchaV2ArgumentResolver != null) {
			resolvers.add(reCaptchaV2ArgumentResolver);
		}
	}

}