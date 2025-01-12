package dev.caceresenzo.recaptcha.v2.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

class ReCaptchaV2ErrorCodeTest {

	static ObjectMapper objectMapper;

	@BeforeAll
	static void setUp() {
		objectMapper = new ObjectMapper();
	}

	@Test
	@SneakyThrows
	void standard() {
		assertEquals(ReCaptchaV2ErrorCode.Standard.MISSING_INPUT_SECRET, objectMapper.readValue("\"missing-input-secret\"", ReCaptchaV2ErrorCode.class));
		assertEquals(ReCaptchaV2ErrorCode.Standard.INVALID_INPUT_SECRET, objectMapper.readValue("\"invalid-input-secret\"", ReCaptchaV2ErrorCode.class));
		assertEquals(ReCaptchaV2ErrorCode.Standard.MISSING_INPUT_RESPONSE, objectMapper.readValue("\"missing-input-response\"", ReCaptchaV2ErrorCode.class));
		assertEquals(ReCaptchaV2ErrorCode.Standard.INVALID_INPUT_RESPONSE, objectMapper.readValue("\"invalid-input-response\"", ReCaptchaV2ErrorCode.class));
		assertEquals(ReCaptchaV2ErrorCode.Standard.INVALID_KEYS, objectMapper.readValue("\"invalid-keys\"", ReCaptchaV2ErrorCode.class));
		assertEquals(ReCaptchaV2ErrorCode.Standard.BAD_REQUEST, objectMapper.readValue("\"bad-request\"", ReCaptchaV2ErrorCode.class));
		assertEquals(ReCaptchaV2ErrorCode.Standard.TIMEOUT_OR_DUPLICATE, objectMapper.readValue("\"timeout-or-duplicate\"", ReCaptchaV2ErrorCode.class));
	}

	@Test
	@SneakyThrows
	void custom() {
		assertEquals(new ReCaptchaV2ErrorCode.Custom("Project is pending deletion"), objectMapper.readValue("\"Project is pending deletion\"", ReCaptchaV2ErrorCode.class));
	}

	@AfterAll
	static void cleanUp() {
		objectMapper = null;
	}

}