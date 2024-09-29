package dev.caceresenzo.recaptcha.spring.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import dev.caceresenzo.recaptcha.spring.web.annotation.ChallengeResponseLocation;

class ReCaptchaV2ValidatorDefaultsTest {

	@Test
	void constructor_noDefault() {
		assertThrows(IllegalArgumentException.class, () -> {
			new ReCaptchaV2ValidatorDefaults(ChallengeResponseLocation.DEFAULT, "", "");
		});
	}

	@Test
	void resolveLocation() {
		final var defaults = new ReCaptchaV2ValidatorDefaults(ChallengeResponseLocation.HEADER, "a", "b");

		assertEquals(ChallengeResponseLocation.HEADER, defaults.resolveLocation(ChallengeResponseLocation.DEFAULT));
		assertEquals(ChallengeResponseLocation.QUERY, defaults.resolveLocation(ChallengeResponseLocation.QUERY));
	}

	@Test
	void resolveName() {
		final var defaults = new ReCaptchaV2ValidatorDefaults(ChallengeResponseLocation.HEADER, "a", "b");

		assertEquals("x", defaults.resolveName(ChallengeResponseLocation.HEADER, "x"));
		assertEquals("x", defaults.resolveName(ChallengeResponseLocation.QUERY, "x"));

		assertEquals("a", defaults.resolveName(ChallengeResponseLocation.HEADER, null));
		assertEquals("a", defaults.resolveName(ChallengeResponseLocation.HEADER, ""));
		assertEquals("a", defaults.resolveName(ChallengeResponseLocation.HEADER, " "));

		assertEquals("b", defaults.resolveName(ChallengeResponseLocation.QUERY, null));
		assertEquals("b", defaults.resolveName(ChallengeResponseLocation.QUERY, ""));
		assertEquals("b", defaults.resolveName(ChallengeResponseLocation.QUERY, " "));

		final var exception = assertThrows(IllegalArgumentException.class, () -> {
			defaults.resolveName(ChallengeResponseLocation.DEFAULT, null);
		});

		assertEquals("unknown name for default location", exception.getMessage());
	}

}