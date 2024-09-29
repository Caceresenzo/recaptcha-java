package dev.caceresenzo.recaptcha.v2.client;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.caceresenzo.recaptcha.ReCaptchaException;
import dev.caceresenzo.recaptcha.v2.ReCaptchaV2Response;
import dev.caceresenzo.recaptcha.v2.ReCaptchaV2Validator;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.ToString;

public class ReCaptchaV2Client implements ReCaptchaV2Validator, AutoCloseable {

	public static final String FORM_MEDIA_TYPE = "application/x-www-form-urlencoded";
	public static final URI VERIFY_URL = URI.create("https://www.google.com/recaptcha/api/siteverify");

	@ToString.Exclude
	private final String secretKey;

	private final HttpClient httpClient;
	private final ObjectMapper objectMapper;

	public ReCaptchaV2Client(@NonNull String secretKey) {
		this.secretKey = secretKey;

		this.httpClient = HttpClient.newBuilder().build();
		this.objectMapper = JsonMapper.builder()
			.addModule(new JavaTimeModule())
			.build();
	}

	public ReCaptchaV2Response verify(String challengeResponse) {
		return verify(challengeResponse, (String) null);
	}

	public ReCaptchaV2Response verify(String challengeResponse, String remoteIp) {
		try {
			return doVerify(challengeResponse, remoteIp);
		} catch (Exception exception) {
			throw new ReCaptchaException("could not validate recaptcha: %s".formatted(exception.getMessage()), exception);
		}
	}

	@SneakyThrows
	public ReCaptchaV2Response doVerify(String challengeResponse, String remoteIp) {
		final var request = HttpRequest.newBuilder()
			.uri(VERIFY_URL)
			.header("Content-Type", FORM_MEDIA_TYPE)
			.POST(BodyPublishers.ofString(buildRequestBody(challengeResponse, remoteIp)))
			.build();

		final var response = httpClient.send(request, BodyHandlers.ofByteArray());
		return map(objectMapper.readValue(response.body(), ReCaptchaV2ApiResponse.class));
	}

	ReCaptchaV2Response map(ReCaptchaV2ApiResponse response) {
		if (response.isSuccess()) {
			return new ReCaptchaV2Response.Success(
				response.getTimestamp(),
				response.getHostname()
			);
		}

		return new ReCaptchaV2Response.Error(
			response.hasClientError(),
			response.toErrorPhrase()
		);
	}

	@Override
	public void close() {
		httpClient.close();
	}

	String buildRequestBody(String challengeResponse, String remoteIp) {
		final var joiner = new StringJoiner("&");

		joiner.add(encode("secret", secretKey));
		joiner.add(encode("response", challengeResponse));

		if (remoteIp != null) {
			joiner.add(encode("remoteip", remoteIp));
		}

		return joiner.toString();
	}

	static String encode(String key, String value) {
		return "%s=%s".formatted(key, URLEncoder.encode(value, StandardCharsets.UTF_8));
	}

}