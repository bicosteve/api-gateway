package com.bicosteve.api_gateway.controllers;

import com.bicosteve.api_gateway.config.AppConfig;
import com.bicosteve.api_gateway.dto.response.TestResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HealthCheckTest {

    @Test
    void testReturnsOkResponse() {
        AppConfig appConfig = mock(AppConfig.class);
        when(appConfig.getPort()).thenReturn(8080);
        HealthCheck healthCheck = new HealthCheck(appConfig);

        ResponseEntity<TestResponse> response = healthCheck.test();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTimestamp());
        assertTrue(response.getBody().getMessage().contains("8080"));
    }
}
