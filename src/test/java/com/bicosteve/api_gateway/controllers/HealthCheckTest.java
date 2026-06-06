package com.bicosteve.api_gateway.controllers;

import com.bicosteve.api_gateway.config.AppConfig;
import com.bicosteve.api_gateway.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)  // ① Initialize @Mock and @InjectMocks
class HealthCheckTest {

    private MockMvc mockMvc;

    @Mock
    private AppConfig appConfig;       // ② Mock the dependency

    @InjectMocks
    private HealthCheck healthCheck;   // ③ Create real controller with mock injected

    @BeforeEach
    void setUp() {
        // ④ Build MockMvc with ONLY our controller (standalone), not the full Spring context
        mockMvc = MockMvcBuilders
                .standaloneSetup(healthCheck)
                .setControllerAdvice(new GlobalExceptionHandler())  // Include exception handlers
                .build();
    }

    @Test
    @DisplayName("GET /api/health/test - Should return 200 with port info")
    void testEndpoint_returns200WithPortInfo() throws Exception {
        // Arrange: tell the mock what to return when getPort() is called
        when(appConfig.getPort()).thenReturn(8080);

        // Act: perform the GET request
        mockMvc.perform(get("/api/health/test"))

                // Assert: verify status is 200 and JSON contains expected values
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("App running on port 8080"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
}
