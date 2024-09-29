package dev.caceresenzo.recaptcha.spring.boot.autoconfigure;

import java.io.IOException;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.caceresenzo.recaptcha.spring.web.ReCaptchaV2ValidatorDefaults;
import dev.caceresenzo.recaptcha.v2.ReCaptchaV2Validator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ReCaptchaV2Properties.class)
@AutoConfigureBefore(ReCaptchaAnnotationWebAutoConfiguration.class)
public class ReCaptchaV2AutoConfiguration {

	@Bean
	@ConditionalOnProperty(ReCaptchaV2Properties.PREFIX_SECRET_KEY)
	@ConditionalOnMissingBean
	ReCaptchaV2Validator reCaptchaVerifier(ReCaptchaV2Properties properties) throws IOException {
		log.info("Configuring reCAPTCHA v2");

		return ReCaptchaV2Validator.builder()
			.secretKey(properties.getSecretKey())
			.build();
	}

	@Bean
	ReCaptchaV2ValidatorDefaults reCaptchaV2ValidatorDefaults(ReCaptchaV2Properties properties) throws IOException {
		final var web = properties.getWeb();

		return new ReCaptchaV2ValidatorDefaults(
			web.getLocation(),
			web.getHeaderName(),
			web.getQueryParameterName()
		);
	}

}