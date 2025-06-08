# reCAPTCHA v2 for Java

This Java client integrates with [Google's reCAPTCHA v2](https://developers.google.com/recaptcha/intro) and supports [Cloudflare's Turnstile](https://developers.cloudflare.com/turnstile/). It includes a Spring Boot starter for easy integration into your applications.

> [!WARNING]
> The API is in beta, expect breaking changes.

- [reCAPTCHA v2 for Java](#recaptcha-v2-for-java)
- [Installation](#installation)
- [Configuration](#configuration)
	- [Cloudflare Turnstile](#cloudflare-turnstile)
- [Usage](#usage)
	- [Verify a Challenge Response](#verify-a-challenge-response)
- [Spring Boot Starter](#spring-boot-starter)
	- [Cloudflare Turnstile](#cloudflare-turnstile-1)
	- [Custom Handling](#custom-handling)
	- [Controller Examples](#controller-examples)
		- [General Case](#general-case)
		- [Manually handling the result](#manually-handling-the-result)
		- [Typed handling of the result](#typed-handling-of-the-result)
		- [Endpoint-specific configuration](#endpoint-specific-configuration)
	- [Spring Doc Integration](#spring-doc-integration)

# Installation

```xml
<properties>
	<recaptcha.version>0.3.0</recaptcha.version>
</properties>

<dependencies>
	<dependency>
		<groupId>dev.caceresenzo.recaptcha</groupId>
		<artifactId>recaptcha-v2-validator</artifactId>
		<version>${recaptcha.version}</version>
	</dependency>
</dependencies>
```

# Configuration

```java
ReCaptchaV2Validator validator = ReCaptchaV2Validator.builder()
	.secretKey("6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI")
	.build();

/* or with the test secret key */
ReCaptchaV2Validator validator = ReCaptchaV2Validator.builder()
	.testSecretKey()
	.build();
```

## Cloudflare Turnstile

```java
ReCaptchaV2Validator validator = ReCaptchaV2Validator.turnstile()
	.secretKey("1x00000000000000000000AA")
	.build();

/* or with the test secret key */
ReCaptchaV2Validator validator = ReCaptchaV2Validator.builder()
	.alwaysPassesTestSecretKey()
	.build();
```

# Usage

## Verify a Challenge Response

```java
ReCaptchaV2Response response = validator.verify("abcdefijklmnopqrstuvwxyz");

/* throws an exception if there is an error */
response.orThrow();

/* use pattern matching to decide */
switch (response) {
	case ReCaptchaV2Response.Success success -> {
		System.out.println("Challenge passed!");
	}

	case ReCaptchaV2Response.Failure failure -> {
		System.err.println("Challenge failed: %s".formatted(failure.message()));
	}
}
```

> [!NOTE]
> The usage is the same for Cloudflare Turnstile.

# Spring Boot Starter

There is a Spring Boot auto-configuration available.

```xml
<dependencies>
	<dependency>
		<groupId>dev.caceresenzo.recaptcha</groupId>
		<artifactId>recaptcha-v2-spring-boot-starter</artifactId>
		<version>${recaptcha.version}</version>
	</dependency>
</dependencies>
```

Which is enabled when the Secret Key is specified in the configuration:

```yml
recaptcha:
  v2:
    secret-key: 6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI

    # Configure the web integration
    web:
      # Change the default response location (either HEADER or QUERY)
      location: QUERY

      # Change the default header name (if the location is HEADER)
      header-name: X-ReCaptcha-Response

      # Change the default query parameter name (if the location is QUERY)
      query-parameter-name: reCaptchaResponse
```

> [!NOTE]
> The default service used is Google reCAPTCHA V2.

## Cloudflare Turnstile

You can use Cloudflare Turnstile simply by setting the `service` property and using the correct secret key. Other configurations and usage remain the same.

```yml
recaptcha:
  v2:
    # Use Cloudflare Turnstile service
    service: CLOUDFLARE_TURNSTILE

    secret-key: 1x0000000000000000000000000000000AA
```

## Custom Handling

If the response is not [manually handled](#manually-handling-the-result), a custom behavior can be specified to handle the result.

```java
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class ReCaptchaConfiguration {

	@Bean
	ReCaptchaV2AnnotationInterceptor.ResponseHandler reCaptchaV2AnnotationInterceptorResponseHandler() {
		return (response) -> response.orThrowWithMessage(MyCustomException::new);
	}

}
```

## Controller Examples

### General Case

To protect an endpoint, simply annotate it with the `@ReCaptchaV2` annotation.

```java
@RestController
@RequestMapping(path = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
public class HelloRestController {

	@ReCaptchaV2
	@GetMapping
	public String noSpam() {
		return "Challenge passed!";
	}

}
```

### Manually handling the result

Instead of having the error thrown automatically, you can manually handle the result by requesting the `ReCaptchaV2Response` parameter.

The behavior is similar to Spring's `BindingResult'.

```java
@ReCaptchaV2
@GetMapping
public String noSpam(
	ReCaptchaV2Response reCaptchaResponse
) {
	if (isSpamProtectionEnabledGlobally()) {
		reCaptchaResponse.orThrow();
	}

	return "Challenge passed!";
}
```

### Typed handling of the result

The behavior will change based on the response type specified in the parameter.

| Parameter Type                | Auto. Throw | How to handle                                                                                           |
| ----------------------------- | ----------- | ------------------------------------------------------------------------------------------------------- |
| `(none)`                      | Yes         | Success is expected by default.<br />Bind errors will be thrown, and the response handler will be used. |
| `ReCaptchaV2Response`         | No          | Response must be handled by the user.<br />Bind errors are muted and treated like regular failures.     |
| `ReCaptchaV2Response.Success` | Yes         | Success is expected.<br />Same behavior as for `(none)`.                                                |
| `ReCaptchaV2Response.Failure` | Partial     | Failure is expected.<br />Bind errors will be thrown, but the response must be handled by the user.     |

> [!NOTE]
> Bind errors are [`MissingRequestHeaderException`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/MissingRequestHeaderException.html) and [`MissingServletRequestParameterException`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/MissingServletRequestParameterException.html). <br />
> If they are muted, they will be replaced with `missing-input-response`.

> [!WARNING]
> A response **might be `null`** if it does not correspond. <br />
> For example, the `ReCaptchaV2Response` is `.Success`, but the required type is `.Failure` (and vice versa).

### Endpoint-specific configuration

If there are some legacy endpoints, they can also be customized to locate the challenge response from a different location than the globally defined one.

```java
@ReCaptchaV2(
	location = ChallengeResponseLocation.QUERY,
	name = "captcha"
)
@GetMapping
public String noSpam() {
	return "Challenge passed!";
}
```

## Spring Doc Integration

When [SpringDoc OpenAPI](https://github.com/springdoc/springdoc-openapi) is detected and an endpoint is annotated with the `@ReCaptchaV2` annotation, the Swagger Operation will automatically have the necessary query/header parameter to the spec.
