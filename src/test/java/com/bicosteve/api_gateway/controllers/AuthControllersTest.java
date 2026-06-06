package com.bicosteve.api_gateway.controllers;

import com.bicosteve.api_gateway.dto.requests.RegisterRequest;
import com.bicosteve.api_gateway.dto.response.RegisterResponse;
import com.bicosteve.api_gateway.exceptions.GlobalExceptionHandler;
import com.bicosteve.api_gateway.exceptions.PhoneNumberExistsException;
import com.bicosteve.api_gateway.service.ProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the {@link AuthControllers#registerUser(RegisterRequest)} endpoint.
 *
 * ─── TESTING STRATEGY ───
 * We use STANDALONE MockMvc (no Spring context) with Mockito mocks:
 * - @Mock ProfileService — we control what the service returns/throws
 * - @Mock HttpServletResponse — the controller needs it but register doesn't use it
 * - @InjectMocks AuthControllers — the REAL controller with mocks injected
 *
 * The GlobalExceptionHandler is registered as controller advice so that
 * @Valid validation errors and exceptions produce the same responses
 * they would in production.
 *
 * ─── NESTED CLASSES ───
 * Tests are grouped using @Nested:
 * - "Happy path" — valid input, successful registration
 * - "Validation edge cases" — bad input that fails @Valid constraints
 * - "Service-layer edge cases" — valid input but service throws an exception
 */
@ExtendWith(MockitoExtension.class)
class AuthControllersTest {

    private MockMvc mockMvc;

    /** ObjectMapper for converting Java objects to JSON in request bodies */
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ProfileService profileService;

    /**
     * The controller declares HttpServletResponse as a dependency.
     * The register endpoint doesn't use it (login does), but we must
     * provide a mock so the constructor can be satisfied.
     */
    @Mock
    private HttpServletResponse httpServletResponse;

    @InjectMocks
    private AuthControllers authControllers;

    @BeforeEach
    void setUp() {
        // Register the GlobalExceptionHandler so validation errors and
        // exception mappings work the same as in production.
        mockMvc = MockMvcBuilders
                .standaloneSetup(authControllers)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // Helpers
    // 01:: Build a valid RegisterRequest with sensible defaults
    private RegisterRequest validRequest() {
        return new RegisterRequest(
                "254701234567",        // valid Kenyan phone
                "test@example.com",    // valid email
                "pass1234",            // password
                "pass1234"             // confirm password (must match)
        );
    }

    // 02:: Build a valid RegisterResponse
    private RegisterResponse validResponse() {
        return RegisterResponse.builder()
                .message("Registration success")
                .verificationCode("123456")
                .build();
    }


    @Nested
    @DisplayName("POST /api/auth/register — Happy Path")
    class HappyPath {

        @Test
        @DisplayName("Should return 201 CREATED with message and verification code")
        void registerUser_validRequest_returns201() throws Exception {
            // 01:: Arrange
            // Tells mock service to return our predefined response when createProfile() is
            // called with RegisterRequest
            RegisterResponse expectedResponse = validResponse();
            when(profileService.createProfile(any(RegisterRequest.class)))
                    .thenReturn(expectedResponse);

            // 02:: Act & Assert
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            // Serialize the Java object to a JSON string body
                            .content(objectMapper.writeValueAsString(validRequest())))
                    // Controller returns HttpStatus.CREATED → 201
                    .andExpect(status().isCreated())
                    // Check each field in the JSON response body
                    .andExpect(jsonPath("$.message").value("Registration success"))
                    .andExpect(jsonPath("$.verificationCode").value("123456"));

            // 03:: Verify the service method was called exactly once.
            verify(profileService, times(1)).createProfile(any(RegisterRequest.class));
        }
    }


    @Nested
    @DisplayName("POST /api/auth/register — Validation Edge Cases")
    class ValidationEdgeCases {

        @Test
        @DisplayName("Should return 400 when phone_number field is blank")
        void registerUser_blankPhone_returns400() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    "",                  // ← blank phone
                    "test@example.com",
                    "pass1234",
                    "pass1234"
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    // GlobalExceptionHandler returns "Validation failed" message
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    // Check the specific field error
                    // NOTE: The key is "phoneNumber" (Java field name), NOT "phone_number" (JSON property).
                    // @JsonProperty only affects JSON serialization, not validation field names.
                    .andExpect(jsonPath("$.validationErrors.phoneNumber").value("Phone number is required"));

            // Service should NEVER be called — validation fails before it reaches the service
            verify(profileService, never()).createProfile(any());
        }

        @Test
        @DisplayName("Should return 400 when phone number format is invalid")
        void registerUser_invalidPhoneFormat_returns400() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    "1234567890",        // ← not a valid Kenyan format (must start with 07, 01, +254)
                    "test@example.com",
                    "pass1234",
                    "pass1234"
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed"));

            verify(profileService, never()).createProfile(any());
        }

        @Test
        @DisplayName("Should return 400 when email is blank")
        void registerUser_blankEmail_returns400() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    "254701234567",
                    "",                  // ← blank email
                    "pass1234",
                    "pass1234"
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.validationErrors.email").value("Email is required"));

            verify(profileService, never()).createProfile(any());
        }

        @Test
        @DisplayName("Should return 400 when password is blank")
        void registerUser_blankPassword_returns400() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    "254701234567",
                    "test@example.com",
                    "",                  // ← blank password
                    "pass1234"
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed"));

            verify(profileService, never()).createProfile(any());
        }

        @Test
        @DisplayName("Should return 400 when passwords do not match")
        void registerUser_passwordMismatch_returns400() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    "254701234567",
                    "test@example.com",
                    "pass1234",
                    "different_password"  // ← doesn't match
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed"));

            verify(profileService, never()).createProfile(any());
        }

        @Test
        @DisplayName("Should return 400 when request body is empty")
        void registerUser_emptyBody_returns400() throws Exception {
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))     // ← completely empty JSON
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed"));

            verify(profileService, never()).createProfile(any());
        }
    }

    // Service Layer Edge case
    @Nested
    @DisplayName("POST /api/auth/register — Service-Layer Edge Cases")
    class ServiceEdgeCases {

        @Test
        @DisplayName("Should return 409 CONFLICT when phone number already exists")
        void registerUser_duplicatePhone_returns409() throws Exception {
            // ── ARRANGE ──
            // The service throws PhoneNumberExistsException if the phone exists.
            // The GlobalExceptionHandler catches it and returns 409.
            when(profileService.createProfile(any(RegisterRequest.class)))
                    .thenThrow(new PhoneNumberExistsException("254701234567"));

            // ── ACT & ASSERT ──
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest())))
                    .andExpect(status().isConflict())  // 409
                    .andExpect(jsonPath("$.message").value(
                            "The phone number 254701234567 already exists"));

            verify(profileService, times(1)).createProfile(any(RegisterRequest.class));
        }

        @Test
        @DisplayName("Should return 500 when service throws unexpected exception")
        void registerUser_serviceThrowsGenericException_returns500() throws Exception {
            // ── ARRANGE ──
            // Simulate an unexpected error (e.g., database down, null pointer, etc.)
            when(profileService.createProfile(any(RegisterRequest.class)))
                    .thenThrow(new RuntimeException("Database connection failed"));

            // ── ACT & ASSERT ──
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest())))
                    .andExpect(status().isInternalServerError());  // 500

            verify(profileService, times(1)).createProfile(any(RegisterRequest.class));
        }
    }
}
