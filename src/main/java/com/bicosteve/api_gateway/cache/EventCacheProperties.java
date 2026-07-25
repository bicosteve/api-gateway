package com.bicosteve.api_gateway.cache;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("event-cache")
public record EventCacheProperties(boolean enabled, @NotBlank String prefix, @NotBlank String version) {
    public String eventKey(String eventId) {
        return namespace() + ":event:" + eventId;
    }

    public String upcomingKey() {
        return namespace() + ":upcoming";
    }

    private String namespace() {
        return prefix + ":" + version;
    }
}
