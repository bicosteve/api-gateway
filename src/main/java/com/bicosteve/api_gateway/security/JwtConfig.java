package com.bicosteve.api_gateway.security;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "spring.jwt")
@Validated
@Data
public class JwtConfig {

    @NotBlank(message = "spring.jwt.secret must not be blank (check JWT_SECRET env var and YAML nesting under 'spring')")
    private String secret;

    @Positive(message = "spring.jwt.access-token-expiration must be a positive number of seconds")
    private int accessTokenExpiration;

    @Positive(message = "spring.jwt.refresh-token-expiration must be a positive number of seconds")
    private int refreshTokenExpiration;
}
