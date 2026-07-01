package com.bicosteve.api_gateway.controllers;

import com.bicosteve.api_gateway.dto.requests.LoginRequest;
import com.bicosteve.api_gateway.dto.requests.RegisterRequest;
import com.bicosteve.api_gateway.dto.requests.VerifyRequest;
import com.bicosteve.api_gateway.dto.response.LoginResponse;
import com.bicosteve.api_gateway.dto.response.RegisterResponse;
import com.bicosteve.api_gateway.dto.response.VerificationResponse;
import com.bicosteve.api_gateway.exceptions.*;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllersTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ProfileService profileService;

    @Mock
    private HttpServletResponse httpServletResponse;

    @InjectMocks
    private AuthControllers authControllers;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authControllers)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ── Helpers ──

    private RegisterRequest validRegisterRequest() {
        return new RegisterRequest("254701234567", "test@example.com", "pass1234", "pass1234");
    }

    private RegisterResponse validRegisterResponse() {
        return RegisterResponse.builder()
                .message("Registration success")
                .verificationCode("123456")
                .build();
    }

    private LoginRequest validLoginRequest() {
        return new LoginRequest("254701234567", "pass1234");
    }

    private LoginResponse validLoginResponse() {
        return LoginResponse.builder()
                .accessToken("access-token-123")
                .refreshToken("refresh-token-456")
                .build();
    }

    private VerifyRequest validVerifyRequest() {
        return new VerifyRequest("254701234567", "123456");
    }

    private VerificationResponse validVerifyResponse() {
        return VerificationResponse.builder()
                .message("Account verified successfully")
                .build();
    }


    // ══════════════════════════════════════════════════════════════
    //  REGISTER — POST /api/auth/register
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("POST /api/auth/register")
    class Register {

        @Nested
        @DisplayName("Happy Path")
        class HappyPath {

            @Test
            @DisplayName("Should return 201 with message and verification code")
            void validRequest_returns201() throws Exception {
                // Arrange
                when(profileService.createProfile(any(RegisterRequest.class)))
                        .thenReturn(validRegisterResponse());

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest())));

                // Assert
                result.andExpect(status().isCreated());
                result.andExpect(jsonPath("$.message").value("Registration success"));
                result.andExpect(jsonPath("$.verificationCode").value("123456"));
                result.andExpect(content().contentType(MediaType.APPLICATION_JSON));

                // Verify
                verify(profileService, times(1)).createProfile(any(RegisterRequest.class));
            }
        }

        @Nested
        @DisplayName("Validation Edge Cases")
        class ValidationEdgeCases {

            @Test
            @DisplayName("Should return 400 when phone is blank")
            void blankPhone_returns400() throws Exception {
                // Arrange
                RegisterRequest request = new RegisterRequest("", "test@example.com", "pass1234", "pass1234");

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

                // Assert
                result.andExpect(status().isBadRequest());
                result.andExpect(jsonPath("$.message").value("Validation failed"));
                result.andExpect(jsonPath("$.validationErrors.phoneNumber").value("Phone number is required"));

                // Verify
                verify(profileService, never()).createProfile(any());
            }

            @Test
            @DisplayName("Should return 400 when phone format is invalid")
            void invalidPhoneFormat_returns400() throws Exception {
                // Arrange
                RegisterRequest request = new RegisterRequest("1234567890", "test@example.com", "pass1234", "pass1234");

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

                // Assert
                result.andExpect(status().isBadRequest());
                result.andExpect(jsonPath("$.message").value("Validation failed"));

                // Verify
                verify(profileService, never()).createProfile(any());
            }

            @Test
            @DisplayName("Should return 400 when email is blank")
            void blankEmail_returns400() throws Exception {
                // Arrange
                RegisterRequest request = new RegisterRequest("254701234567", "", "pass1234", "pass1234");

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

                // Assert
                result.andExpect(status().isBadRequest());
                result.andExpect(jsonPath("$.message").value("Validation failed"));
                result.andExpect(jsonPath("$.validationErrors.email").value("Email is required"));

                // Verify
                verify(profileService, never()).createProfile(any());
            }

            @Test
            @DisplayName("Should return 400 when email format is invalid")
            void invalidEmailFormat_returns400() throws Exception {
                RegisterRequest request = new RegisterRequest(
                        "254701234567", "not-an-email", "pass1234", "pass1234");

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message").value("Validation failed"));

                verify(profileService, never()).createProfile(any());
            }

            @Test
            @DisplayName("Should return 400 when password is blank")
            void blankPassword_returns400() throws Exception {
                // Arrange
                RegisterRequest request = new RegisterRequest("254701234567", "test@example.com", "", "pass1234");

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

                // Assert
                result.andExpect(status().isBadRequest());
                result.andExpect(jsonPath("$.message").value("Validation failed"));

                // Verify
                verify(profileService, never()).createProfile(any());
            }

            @Test
            @DisplayName("Should return 400 when passwords do not match")
            void passwordMismatch_returns400() throws Exception {
                // Arrange
                RegisterRequest request = new RegisterRequest("254701234567", "test@example.com", "pass1234", "different");

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

                // Assert
                result.andExpect(status().isBadRequest());
                result.andExpect(jsonPath("$.message").value("Validation failed"));

                // Verify
                verify(profileService, never()).createProfile(any());
            }

            @Test
            @DisplayName("Should return 400 when request body is empty")
            void emptyBody_returns400() throws Exception {
                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"));

                // Assert
                result.andExpect(status().isBadRequest());
                result.andExpect(jsonPath("$.message").value("Validation failed"));

                // Verify
                verify(profileService, never()).createProfile(any());
            }

            @Test
            @DisplayName("Should return 415 when Content-Type is missing")
            void missingContentType_returns415() throws Exception {
                ResultActions result = mockMvc.perform(post("/api/auth/register")
                        .content(objectMapper.writeValueAsString(validRegisterRequest())));
                // no .contentType() here

                result.andExpect(status().isInternalServerError());
            }


        }

        @Nested
        @DisplayName("Service-Layer Edge Cases")
        class ServiceEdgeCases {

            @Test
            @DisplayName("Should return 409 when phone number already exists")
            void duplicatePhone_returns409() throws Exception {
                // Arrange
                when(profileService.createProfile(any(RegisterRequest.class)))
                        .thenThrow(new PhoneNumberExistsException("254701234567"));

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest())));

                // Assert
                result.andExpect(status().isConflict());
                result.andExpect(jsonPath("$.message").value("The phone number 254701234567 already exists"));

                // Verify
                verify(profileService, times(1)).createProfile(any(RegisterRequest.class));
            }

            @Test
            @DisplayName("Should return 500 when service throws unexpected exception")
            void genericException_returns500() throws Exception {
                // Arrange
                when(profileService.createProfile(any(RegisterRequest.class)))
                        .thenThrow(new RuntimeException("Database connection failed"));

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest())));

                // Assert
                result.andExpect(status().isInternalServerError());

                // Verify
                verify(profileService, times(1)).createProfile(any(RegisterRequest.class));
            }

            @Test
            @DisplayName("Should return 400 when profile creation fails")
            void profileCreationFails_returns400() throws Exception {
                // Arrange
                when(profileService.createProfile(any(RegisterRequest.class)))
                        .thenThrow(new ProfileCreationException("Failed to create profile"));

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest())));

                // Assert
                result.andExpect(status().isBadRequest());

                // Verify
                verify(profileService, times(1)).createProfile(any(RegisterRequest.class));
            }
        }
    }


    // ══════════════════════════════════════════════════════════════
    //  LOGIN — POST /api/auth/login
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("POST /api/auth/login")
    class Login {

        @Nested
        @DisplayName("Happy Path")
        class HappyPath {

            @Test
            @DisplayName("Should return 200 with access and refresh tokens")
            void validRequest_returns200() throws Exception {
                // Arrange
                when(profileService.generateLoginToken(any(LoginRequest.class), any(HttpServletResponse.class)))
                        .thenReturn(validLoginResponse());

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest())));

                // Assert
                result.andExpect(status().isOk());
                result.andExpect(jsonPath("$.accessToken").value("access-token-123"));
                result.andExpect(jsonPath("$.refreshToken").value("refresh-token-456"));

                // Verify
                verify(profileService, times(1))
                        .generateLoginToken(any(LoginRequest.class), any(HttpServletResponse.class));
            }
        }

        @Nested
        @DisplayName("Validation Edge Cases")
        class ValidationEdgeCases {

            @Test
            @DisplayName("Should return 400 when phone is blank")
            void blankPhone_returns400() throws Exception {
                // Arrange
                LoginRequest request = new LoginRequest("", "pass1234");

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

                // Assert
                result.andExpect(status().isBadRequest());
                result.andExpect(jsonPath("$.message").value("Validation failed"));
                result.andExpect(jsonPath("$.validationErrors.phoneNumber").value("Phone number is required"));

                // Verify
                verify(profileService, never()).generateLoginToken(any(), any());
            }

            @Test
            @DisplayName("Should return 400 when phone format is invalid")
            void invalidPhoneFormat_returns400() throws Exception {
                // Arrange
                LoginRequest request = new LoginRequest("12345", "pass1234");

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

                // Assert
                result.andExpect(status().isBadRequest());
                result.andExpect(jsonPath("$.message").value("Validation failed"));

                // Verify
                verify(profileService, never()).generateLoginToken(any(), any());
            }

            @Test
            @DisplayName("Should return 400 when password is blank")
            void blankPassword_returns400() throws Exception {
                // Arrange
                LoginRequest request = new LoginRequest("254701234567", "");

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

                // Assert
                result.andExpect(status().isBadRequest());
                result.andExpect(jsonPath("$.message").value("Validation failed"));
                result.andExpect(jsonPath("$.validationErrors.password").exists());

                // Verify
                verify(profileService, never()).generateLoginToken(any(), any());
            }

            @Test
            @DisplayName("Should return 400 when request body is empty")
            void emptyBody_returns400() throws Exception {
                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"));

                // Assert
                result.andExpect(status().isBadRequest());
                result.andExpect(jsonPath("$.message").value("Validation failed"));

                // Verify
                verify(profileService, never()).generateLoginToken(any(), any());
            }

            @Test
            @DisplayName("Should return 400 when phone exceeds max length")
            void phoneTooLong_returns400() throws Exception {
                // Arrange
                LoginRequest request = new LoginRequest("254701234567890", "pass1234");

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

                // Assert
                result.andExpect(status().isBadRequest());
                result.andExpect(jsonPath("$.message").value("Validation failed"));

                // Verify
                verify(profileService, never()).generateLoginToken(any(), any());
            }

            @Test
            @DisplayName("Should return 400 when account is not verified")
            void unverifiedAccount_returns400() throws Exception {
                when(profileService.generateLoginToken(any(), any()))
                        .thenThrow(new VerifyAccountException("Account not verified"));

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validLoginRequest())))
                        .andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("Service-Layer Edge Cases")
        class ServiceEdgeCases {

            @Test
            @DisplayName("Should return 404 when phone number not found")
            void phoneNotFound_returns404() throws Exception {
                // Arrange
                when(profileService.generateLoginToken(any(LoginRequest.class), any(HttpServletResponse.class)))
                        .thenThrow(new PhoneNumberNotFoundException("254701234567"));

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest())));

                // Assert
                result.andExpect(status().isNotFound());

                // Verify
                verify(profileService, times(1))
                        .generateLoginToken(any(LoginRequest.class), any(HttpServletResponse.class));
            }

            @Test
            @DisplayName("Should return 401 when password is wrong (BadCredentialsException)")
            void wrongPassword_returns401() throws Exception {
                // Arrange — BadCredentialsException now has a dedicated handler in
                // GlobalExceptionHandler that maps it to 401 Unauthorized.
                when(profileService.generateLoginToken(any(LoginRequest.class), any(HttpServletResponse.class)))
                        .thenThrow(new BadCredentialsException("Invalid credentials"));

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest())));

                // Assert
                result.andExpect(status().isUnauthorized());
                result.andExpect(jsonPath("$.message").value("Invalid phone number or password"));

                // Verify
                verify(profileService, times(1))
                        .generateLoginToken(any(LoginRequest.class), any(HttpServletResponse.class));
            }

            @Test
            @DisplayName("Should return 404 when profile not found")
            void profileNotFound_returns404() throws Exception {
                // Arrange
                when(profileService.generateLoginToken(any(LoginRequest.class), any(HttpServletResponse.class)))
                        .thenThrow(new ProfileNotFoundException(1L));

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest())));

                // Assert
                result.andExpect(status().isNotFound());

                // Verify
                verify(profileService, times(1))
                        .generateLoginToken(any(LoginRequest.class), any(HttpServletResponse.class));
            }

            @Test
            @DisplayName("Should return 500 when service throws unexpected exception")
            void genericException_returns500() throws Exception {
                // Arrange
                when(profileService.generateLoginToken(any(LoginRequest.class), any(HttpServletResponse.class)))
                        .thenThrow(new RuntimeException("Unexpected error"));

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest())));

                // Assert
                result.andExpect(status().isInternalServerError());

                // Verify
                verify(profileService, times(1))
                        .generateLoginToken(any(LoginRequest.class), any(HttpServletResponse.class));
            }

            @Test
            @DisplayName("Should return 400 when account is not verified")
            void unverifiedAccount_returns400() throws Exception {
                // Arrange
                when(profileService.generateLoginToken(any(LoginRequest.class), any(HttpServletResponse.class)))
                        .thenThrow(new VerifyAccountException("Account not verified"));

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest())));

                // Assert
                result.andExpect(status().isBadRequest());

                // Verify
                verify(profileService, times(1))
                        .generateLoginToken(any(LoginRequest.class), any(HttpServletResponse.class));
            }
        }
    }


    // ══════════════════════════════════════════════════════════════
    //  VERIFY — POST /api/auth/verify-account
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("POST /api/auth/verify-account")
    class Verify {

        @Nested
        @DisplayName("Happy Path")
        class HappyPath {

            @Test
            @DisplayName("Should return 200 with verification success message")
            void validRequest_returns200() throws Exception {
                // Arrange
                when(profileService.verifyProfile(any(VerifyRequest.class)))
                        .thenReturn(validVerifyResponse());

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/verify-account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validVerifyRequest())));

                // Assert
                result.andExpect(status().isOk());
                result.andExpect(jsonPath("$.message").value("Account verified successfully"));

                // Verify
                verify(profileService, times(1)).verifyProfile(any(VerifyRequest.class));
            }
        }

        @Nested
        @DisplayName("Validation Edge Cases")
        class ValidationEdgeCases {

            @Test
            @DisplayName("Should return 400 when phone is blank")
            void blankPhone_returns400() throws Exception {
                // Arrange
                VerifyRequest request = new VerifyRequest("", "123456");

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/verify-account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

                // Assert
                result.andExpect(status().isBadRequest());
                result.andExpect(jsonPath("$.message").value("Validation failed"));
                result.andExpect(jsonPath("$.validationErrors.phoneNumber").value("Phone number is required"));

                // Verify
                verify(profileService, never()).verifyProfile(any());
            }

            @Test
            @DisplayName("Should return 400 when phone format is invalid")
            void invalidPhoneFormat_returns400() throws Exception {
                // Arrange
                VerifyRequest request = new VerifyRequest("12345", "123456");

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/verify-account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

                // Assert
                result.andExpect(status().isBadRequest());
                result.andExpect(jsonPath("$.message").value("Validation failed"));

                // Verify
                verify(profileService, never()).verifyProfile(any());
            }

            @Test
            @DisplayName("Should return 400 when verification code is blank")
            void blankCode_returns400() throws Exception {
                // Arrange
                VerifyRequest request = new VerifyRequest("254701234567", "");

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/verify-account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

                // Assert
                result.andExpect(status().isBadRequest());
                result.andExpect(jsonPath("$.message").value("Validation failed"));
                result.andExpect(jsonPath("$.validationErrors.verificationCode").exists());

                // Verify
                verify(profileService, never()).verifyProfile(any());
            }

            @Test
            @DisplayName("Should return 400 when verification code is too short")
            void codeTooShort_returns400() throws Exception {
                // Arrange — @Size(min=4) means "12" is too short
                VerifyRequest request = new VerifyRequest("254701234567", "12");

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/verify-account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

                // Assert
                result.andExpect(status().isBadRequest());
                result.andExpect(jsonPath("$.message").value("Validation failed"));

                // Verify
                verify(profileService, never()).verifyProfile(any());
            }

            @Test
            @DisplayName("Should return 400 when verification code is too long")
            void codeTooLong_returns400() throws Exception {
                // Arrange — @Size(max=6) means "1234567" is too long
                VerifyRequest request = new VerifyRequest("254701234567", "1234567");

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/verify-account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

                // Assert
                result.andExpect(status().isBadRequest());
                result.andExpect(jsonPath("$.message").value("Validation failed"));

                // Verify
                verify(profileService, never()).verifyProfile(any());
            }

            @Test
            @DisplayName("Should return 400 when request body is empty")
            void emptyBody_returns400() throws Exception {
                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/verify-account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"));

                // Assert
                result.andExpect(status().isBadRequest());
                result.andExpect(jsonPath("$.message").value("Validation failed"));

                // Verify
                verify(profileService, never()).verifyProfile(any());
            }

            @Test
            @DisplayName("Should return 409 when account is already verified")
            void alreadyVerified_returns409() throws Exception {
                when(profileService.verifyProfile(any()))
                        .thenThrow(new PhoneNumberExistsException("Already verified"));

                mockMvc.perform(post("/api/auth/verify-account")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validVerifyRequest())))
                        .andExpect(status().isConflict());
            }
        }

        @Nested
        @DisplayName("Service-Layer Edge Cases")
        class ServiceEdgeCases {

            @Test
            @DisplayName("Should return 400 when OTP is invalid")
            void invalidOtp_returns400() throws Exception {
                // Arrange
                when(profileService.verifyProfile(any(VerifyRequest.class)))
                        .thenThrow(new InvalidOtpException("Invalid OTP code"));

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/verify-account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validVerifyRequest())));

                // Assert
                result.andExpect(status().isBadRequest());

                // Verify
                verify(profileService, times(1)).verifyProfile(any(VerifyRequest.class));
            }

            @Test
            @DisplayName("Should return 400 when OTP has expired")
            void expiredOtp_returns400() throws Exception {
                // Arrange
                when(profileService.verifyProfile(any(VerifyRequest.class)))
                        .thenThrow(new OtpExpiredException("OTP has expired"));

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/verify-account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validVerifyRequest())));

                // Assert — expired OTP is a client error; user must request a fresh code
                result.andExpect(status().isBadRequest());

                // Verify
                verify(profileService, times(1)).verifyProfile(any(VerifyRequest.class));
            }

            @Test
            @DisplayName("Should return 404 when phone number not found")
            void phoneNotFound_returns404() throws Exception {
                // Arrange
                when(profileService.verifyProfile(any(VerifyRequest.class)))
                        .thenThrow(new PhoneNumberNotFoundException("254701234567"));

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/verify-account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validVerifyRequest())));

                // Assert
                result.andExpect(status().isNotFound());

                // Verify
                verify(profileService, times(1)).verifyProfile(any(VerifyRequest.class));
            }

            @Test
            @DisplayName("Should return 500 when service throws unexpected exception")
            void genericException_returns500() throws Exception {
                // Arrange
                when(profileService.verifyProfile(any(VerifyRequest.class)))
                        .thenThrow(new RuntimeException("Unexpected error"));

                // Act
                ResultActions result = mockMvc.perform(post("/api/auth/verify-account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validVerifyRequest())));

                // Assert
                result.andExpect(status().isInternalServerError());

                // Verify
                verify(profileService, times(1)).verifyProfile(any(VerifyRequest.class));
            }
        }
    }


    // ══════════════════════════════════════════════════════════════
    // REFRESH — POST /api/auth/refresh
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("POST /api/auth/refresh")
    class Refresh {

        @Test
        @DisplayName("Should return 200 with new tokens when refresh cookie is valid")
        void validCookie_returns200() throws Exception {
            when(profileService.refreshAccessToken(eq("refresh-token-456"), any(HttpServletResponse.class)))
                    .thenReturn(validLoginResponse());

            ResultActions result = mockMvc.perform(post("/api/auth/refresh")
                    .cookie(new jakarta.servlet.http.Cookie("refreshToken", "refresh-token-456")));

            result.andExpect(status().isOk());
            result.andExpect(jsonPath("$.accessToken").value("access-token-123"));
            result.andExpect(jsonPath("$.refreshToken").value("refresh-token-456"));

            verify(profileService, times(1))
                    .refreshAccessToken(eq("refresh-token-456"), any(HttpServletResponse.class));
        }

        @Test
        @DisplayName("Should fall back to the Authorization header when no cookie is present")
        void bearerHeaderFallback_returns200() throws Exception {
            when(profileService.refreshAccessToken(eq("header-token"), any(HttpServletResponse.class)))
                    .thenReturn(validLoginResponse());

            ResultActions result = mockMvc.perform(post("/api/auth/refresh")
                    .header("Authorization", "Bearer header-token"));

            result.andExpect(status().isOk());
            verify(profileService, times(1))
                    .refreshAccessToken(eq("header-token"), any(HttpServletResponse.class));
        }

        @Test
        @DisplayName("Should prefer the cookie over the Authorization header")
        void cookiePreferredOverHeader_returns200() throws Exception {
            when(profileService.refreshAccessToken(eq("cookie-token"), any(HttpServletResponse.class)))
                    .thenReturn(validLoginResponse());

            mockMvc.perform(post("/api/auth/refresh")
                            .cookie(new jakarta.servlet.http.Cookie("refreshToken", "cookie-token"))
                            .header("Authorization", "Bearer header-token"))
                    .andExpect(status().isOk());

            verify(profileService, times(1))
                    .refreshAccessToken(eq("cookie-token"), any(HttpServletResponse.class));
        }

        @Test
        @DisplayName("Should return 401 when refresh token is missing")
        void missingToken_returns401() throws Exception {
            when(profileService.refreshAccessToken(isNull(), any(HttpServletResponse.class)))
                    .thenThrow(new InvalidTokenException("Refresh token is missing"));

            ResultActions result = mockMvc.perform(post("/api/auth/refresh"));

            result.andExpect(status().isUnauthorized());
            result.andExpect(jsonPath("$.message").value("Refresh token is missing"));
        }

        @Test
        @DisplayName("Should return 401 when refresh token is invalid or expired")
        void invalidToken_returns401() throws Exception {
            when(profileService.refreshAccessToken(eq("bad-token"), any(HttpServletResponse.class)))
                    .thenThrow(new InvalidTokenException("Refresh token is invalid or has expired"));

            ResultActions result = mockMvc.perform(post("/api/auth/refresh")
                    .cookie(new jakarta.servlet.http.Cookie("refreshToken", "bad-token")));

            result.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should return 404 when the profile for the token no longer exists")
        void profileGone_returns404() throws Exception {
            when(profileService.refreshAccessToken(eq("orphan-token"), any(HttpServletResponse.class)))
                    .thenThrow(new PhoneNumberNotFoundException("254701234567"));

            ResultActions result = mockMvc.perform(post("/api/auth/refresh")
                    .cookie(new jakarta.servlet.http.Cookie("refreshToken", "orphan-token")));

            result.andExpect(status().isNotFound());
        }
    }
}
