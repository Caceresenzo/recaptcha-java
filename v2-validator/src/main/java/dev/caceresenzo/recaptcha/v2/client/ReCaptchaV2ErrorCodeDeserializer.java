package dev.caceresenzo.recaptcha.v2.client;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@SuppressWarnings("serial")
public class ReCaptchaV2ErrorCodeDeserializer extends StdDeserializer<ReCaptchaV2ErrorCode> {

	private static final Map<String, ReCaptchaV2ErrorCode.Standard> STANDARD_MAPPING = Map.of(
		"missing-input-secret", ReCaptchaV2ErrorCode.Standard.MISSING_INPUT_SECRET,
		"invalid-input-secret", ReCaptchaV2ErrorCode.Standard.INVALID_INPUT_SECRET,
		"missing-input-response", ReCaptchaV2ErrorCode.Standard.MISSING_INPUT_RESPONSE,
		"invalid-input-response", ReCaptchaV2ErrorCode.Standard.INVALID_INPUT_RESPONSE,
		"invalid-keys", ReCaptchaV2ErrorCode.Standard.INVALID_KEYS,
		"bad-request", ReCaptchaV2ErrorCode.Standard.BAD_REQUEST,
		"timeout-or-duplicate", ReCaptchaV2ErrorCode.Standard.TIMEOUT_OR_DUPLICATE
	);

	public ReCaptchaV2ErrorCodeDeserializer() {
		super(ReCaptchaV2ErrorCode.class);
	}

	@Override
	public ReCaptchaV2ErrorCode deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
		final var message = parser.getText();

		final var standard = STANDARD_MAPPING.get(message);
		if (standard != null) {
			return standard;
		}

		return new ReCaptchaV2ErrorCode.Custom(message);
	}

}