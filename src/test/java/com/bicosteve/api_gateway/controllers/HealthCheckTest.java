package com.bicosteve.api_gateway.controllers;

import com.bicosteve.api_gateway.config.AppConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the {@link HealthCheck} controller.
 *
 * This uses a STANDALONE MockMvc setup — no Spring context is loaded.
 * We manually create the controller with mocked dependencies.
 *
 * Why standalone?
 * - Faster: no Spring context to boot
 * - Simpler: no bean wiring issues with SecurityConfig, JwtService, etc.
 * - Focused: tests ONLY the controller logic
 *
 * @ExtendWith(MockitoExtension.class) — Enables Mockito annotations like @Mock and @InjectMocks.
 * @Mock — Creates a mock object (no real behavior unless we stub it).
 * @InjectMocks — Creates the real controller and injects the mocks into it.
 */
@ExtendWith(MockitoExtension.class)
class HealthCheckTest {

    /**
     * MockMvc is built manually in @BeforeEach using standaloneSetup.
     * It lets us send fake HTTP requests to our controller and inspect the responses.
     */
    private MockMvc mockMvc;

    /**
     * We mock AppConfig because the controller needs AppConfig.getPort().
     * In a real app, this would be populated from application.yml.
     */
    @Mock
    private AppConfig appConfig;

    /**
     * @InjectMocks creates a REAL HealthCheck instance and injects the mocked
     * AppConfig into it (via constructor, since the controller uses @RequiredArgsConstructor).
     */
    @InjectMocks
    private HealthCheck healthCheck;

    /**
     * Before each test, we build MockMvc in standalone mode.
     * This registers our controller without any Spring context.
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(healthCheck).build();
    }

    /**
     * Test: GET /api/health/test should return 200 OK with the correct message.
     *
     * ARRANGE → ACT → ASSERT pattern:
     * 1. ARRANGE: Set up mocks and expected values
     * 2. ACT: Perform the HTTP request
     * 3. ASSERT: Verify the response matches expectations
     */
    @Test
    void testEndpoint_shouldReturnOkWithMessage() throws Exception {
        // ARRANGE: Tell the mock to return 8080 when getPort() is called
        when(appConfig.getPort()).thenReturn(5001);

        // ACT & ASSERT: Send a GET request and verify the response
        mockMvc.perform(get("/api/health/test"))
                // Verify HTTP status is 200 OK
                .andExpect(status().isOk())
                // Verify the JSON "message" field equals our expected string
                .andExpect(jsonPath("$.message").value("App running on port 5001"))
                // Verify the JSON "timestamp" field exists (we don't check the exact value
                // because it's LocalDateTime.now() which changes every time)
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
