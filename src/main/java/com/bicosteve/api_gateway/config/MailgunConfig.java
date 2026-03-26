package com.bicosteve.api_gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "mailgun")
public class MailgunConfig{
    private String apiKey;
    private String baseUrl;
    private String sandbox;
    private String from;
}
