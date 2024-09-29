package dev.caceresenzo.recaptcha.v2.client;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dev.caceresenzo.recaptcha.ReCaptchaException;
import dev.caceresenzo.recaptcha.v2.ReCaptchaV2Response;

class ReCaptchaV2ClientTest {

	@AutoClose
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

		final var errorResponse = new ReCaptchaV2Response.Error(true, ReCaptchaV2ApiResponse.ErrorCode.INVALID_INPUT_RESPONSE.toMessage());
		assertEquals(
			errorResponse,
			client.map(
				new ReCaptchaV2ApiResponse()
					.setSuccess(false)
					.setErrorCodes(Collections.singletonList(ReCaptchaV2ApiResponse.ErrorCode.INVALID_INPUT_RESPONSE))
			)
		);

		assertDoesNotThrow(successResponse::orThrows);
		assertThrows(ReCaptchaException.class, errorResponse::orThrows);
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