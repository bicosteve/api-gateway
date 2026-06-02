package com.bicosteve.api_gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "chapa")
@Data
public class ChapaConfig {
    private String secretKey;
    private String baseUrl;
    private String webhookSecret;
    private String currency;
    private String callbackUrl;
    private String returnUrl;
}
