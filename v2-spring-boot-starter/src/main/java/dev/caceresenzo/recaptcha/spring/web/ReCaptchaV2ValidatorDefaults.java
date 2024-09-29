package dev.caceresenzo.recaptcha.spring.web;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import dev.caceresenzo.recaptcha.spring.web.annotation.ChallengeResponseLocation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.SneakyThrows;

public record ReCaptchaV2ValidatorDefaults(
	@NonNull ChallengeResponseLocation location,
	@NonNull String headerName,
	@NonNull String queryParameterName
) {

	private static final MethodParameter HEADER_METHOD = getRecaptchaHeaderMethod();

	public ReCaptchaV2ValidatorDefaults {
		if (ChallengeResponseLocation.DEFAULT.equals(location)) {
			throw new IllegalArgumentException("default location must be specified");
		}
	}

	@SneakyThrows
	public String resolve(
		ChallengeResponseLocation location,
		String name,
		HttpServletRequest request
	) {
		location = resolveLocation(location);
		name = resolveName(location, name);

		final var value = switch (location) {
			case HEADER -> request.getHeader(name);
			case QUERY -> request.getParameter(name);
			case DEFAULT -> throw new IllegalStateException();
		};

		if (value != null && !value.isBlank()) {
			return value;
		}

		throw switch (location) {
			case HEADER -> new MissingRequestHeaderException(name, HEADER_METHOD);
			case QUERY -> new MissingServletRequestParameterException(name, String.class.getSimpleName());
			case DEFAULT -> new IllegalStateException();
		};
	}

	public ChallengeResponseLocation resolveLocation(ChallengeResponseLocation location) {
		if (ChallengeResponseLocation.DEFAULT.equals(location)) {
			return this.location;
		}

		return location;
	}

	public String resolveName(ChallengeResponseLocation location, String name) {
		if (name != null && !name.isBlank()) {
			return name;
		}

		return switch (location) {
			case HEADER -> headerName;
			case QUERY -> queryParameterName;
			case DEFAULT -> throw new IllegalArgumentException("unknown name for default location");
		};
	}

	@SuppressWarnings("unused")
	private static void recaptchaHeader(String header) {}

	@SneakyThrows
	private static MethodParameter getRecaptchaHeaderMethod() {
		return new MethodParameter(ReCaptchaV2ValidatorDefaults.class.getDeclaredMethod("recaptchaHeader", String.class), 0);
	}

}