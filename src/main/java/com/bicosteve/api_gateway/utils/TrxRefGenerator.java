package com.bicosteve.api_gateway.utils;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TrxRefGenerator {

    public String generateTrxRef(Long profileId) {
        return "DEPOSIT-%s-%s-%s".formatted(
                profileId,
                System.currentTimeMillis(),
                UUID.randomUUID().toString().substring(0,8).toLowerCase());
    }
}
