# ReCAPTCHA v2 for Java

> [!WARNING]
> The API is in beta, expect breaking changes.

- [ReCAPTCHA v2 for Java](#recaptcha-v2-for-java)
  - [Installation](#installation)
  - [Configuration](#configuration)
  - [Usage](#usage)
    - [Verify a Challenge Response](#verify-a-challenge-response)
  - [Spring Boot Starter](#spring-boot-starter)
    - [Controller Example](#controller-example)

## Installation

```xml
<properties>
    <recaptcha.version>0.1.0</recaptcha.version>
</properties>

<dependencies>
    <dependency>
        <groupId>dev.caceresenzo.recaptcha</groupId>
        <artifactId>recaptcha-v2-validator</artifactId>
        <version>${recaptcha.version}</version>
    </dependency>
</dependencies>
```

## Configuration

```java
ReCaptchaV2Validator validator = ReCaptchaV2Validator.builder()
    .secretKey("6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI")
    .build();

/* or with the test secret key */
ReCaptchaV2Validator validator = ReCaptchaV2Validator.builder()
    .testSecretKey()
    .build();
```

## Usage

### Verify a Challenge Response

```java
ReCaptchaV2Response response = validator.verify("abcdefijklmnopqrstuvwxyz");

/* throws an exception if there is an error */
response.orThrows();

/* use pattern matching to decide */
switch (response) {
    case ReCaptchaV2Response.Success success -> {
        System.out.println("Challenge passed!");
    }

    case ReCaptchaV2Response.Error error -> {
        System.err.println("Challenge failed: %s".formatted(error.message()));
    }
}
```

## Spring Boot Starter

There is a Spring Boot auto-configuration available.

```xml
<dependencies>
    <dependency>
        <groupId>dev.caceresenzo.recaptcha</groupId>
        <artifactId>recaptcha-spring-boot-starter</artifactId>
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

### Controller Example

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
