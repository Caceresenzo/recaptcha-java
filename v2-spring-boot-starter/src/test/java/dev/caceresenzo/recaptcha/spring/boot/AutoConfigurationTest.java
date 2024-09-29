package dev.caceresenzo.recaptcha.spring.boot;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dev.caceresenzo.recaptcha.spring.boot.autoconfigure.ReCaptchaAnnotationWebAutoConfiguration;
import dev.caceresenzo.recaptcha.spring.boot.autoconfigure.ReCaptchaV2AutoConfiguration;
import dev.caceresenzo.recaptcha.spring.boot.autoconfigure.ReCaptchaV2Properties;
import dev.caceresenzo.recaptcha.spring.web.ReCaptchaV2AnnotationInterceptor;
import dev.caceresenzo.recaptcha.v2.ReCaptchaV2Validator;

@SpringBootTest(
	classes = {
		ReCaptchaV2AutoConfiguration.class,
		ReCaptchaAnnotationWebAutoConfiguration.class,
	}, properties = {
		ReCaptchaV2Properties.PREFIX_SECRET_KEY + "=test"
	}
)
class AutoConfigurationTest {

	@Autowired
	ReCaptchaV2Validator reCaptchaV2Validator;

	@Autowired
	ReCaptchaV2AnnotationInterceptor reCaptchaV2AnnotationInterceptor;

	@Test
	void contextLoads() {
		assertNotNull(reCaptchaV2Validator);
		assertNotNull(reCaptchaV2AnnotationInterceptor);
	}

}