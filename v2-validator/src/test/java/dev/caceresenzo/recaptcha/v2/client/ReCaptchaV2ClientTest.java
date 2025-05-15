package dev.caceresenzo.recaptcha.v2.client;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dev.caceresenzo.recaptcha.ReCaptchaException;
import dev.caceresenzo.recaptcha.v2.ReCaptchaV2Response;

class ReCaptchaV2ClientTest {

	static ReCaptchaV2Client client;

	@BeforeAll
	static void setUp() {
		client = new ReCaptchaV2Client("abc");
	}

	@Test
	void map() {
		final var now = LocalDateTime.now();
		final var hostname = "example.com";

		final var successResponse = new ReCaptchaV2Response.Success(now, "example.com");
		assertEquals(
			successResponse,
			client.map(
				new ReCaptchaV2ApiResponse()
					.setSuccess(true)
					.setHostname(hostname)
					.setTimestamp(now)
			)
		);

		final var errorResponse = new ReCaptchaV2Response.Failure(true, ReCaptchaV2ErrorCode.Standard.INVALID_INPUT_RESPONSE.message());
		assertEquals(
			errorResponse,
			client.map(
				new ReCaptchaV2ApiResponse()
					.setSuccess(false)
					.setErrorCodes(Collections.singletonList(ReCaptchaV2ErrorCode.Standard.INVALID_INPUT_RESPONSE))
			)
		);

		assertDoesNotThrow(() -> successResponse.orThrow());
		assertThrows(ReCaptchaException.class, errorResponse::orThrow);
	}

	@Test
	void buildRequestBody() {
		assertEquals("secret=abc&response=def", client.buildRequestBody("def", null));
		assertEquals("secret=abc&response=def&remoteip=ghi", client.buildRequestBody("def", "ghi"));
	}

	@Test
	void encode() {
		assertEquals("a=b", ReCaptchaV2Client.encode("a", "b"));
		assertEquals("a=%3D%3D", ReCaptchaV2Client.encode("a", "=="));
	}

}