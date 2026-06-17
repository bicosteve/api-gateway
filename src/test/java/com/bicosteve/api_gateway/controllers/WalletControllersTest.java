package com.bicosteve.api_gateway.controllers;

import com.bicosteve.api_gateway.dto.requests.DepositRequest;
import com.bicosteve.api_gateway.dto.response.DepositResponse;
import com.bicosteve.api_gateway.payments.ChapaService;
import com.bicosteve.api_gateway.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class WalletControllersTest {

    private ChapaService chapaService;
    private WalletControllers controller;

    @BeforeEach
    void setUp() {
        chapaService = mock(ChapaService.class);
        controller = new WalletControllers(chapaService);
    }

    private Authentication auth() {
        CustomUserDetails cud = new CustomUserDetails(7L, "254701234567",
                1, 1, 0, "x", List.of());
        return new UsernamePasswordAuthenticationToken(cud, null, List.of());
    }

    @Test
    void depositChapaReturns201() {
        DepositRequest req = DepositRequest.builder()
                .amount(BigDecimal.valueOf(100))
                .email("a@b.com")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("254701234567")
                .build();
        DepositResponse response = DepositResponse.builder()
                .trxRef("TRX-1").checkoutUrl("https://x").amount(BigDecimal.valueOf(100))
                .currency("KES").profileId(7L).build();
        when(chapaService.initiateDeposit(any(Authentication.class), eq(req)))
                .thenReturn(response);

        ResponseEntity<DepositResponse> r = controller.depositChapa(req, auth());

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        assertEquals("TRX-1", r.getBody().getTrxRef());
    }

    @Test
    void webhookReturns200OnSuccess() {
        ResponseEntity<Void> r = controller.chapaWebhook(
                "sig", null, "TRX-1", "{}");
        assertEquals(HttpStatus.OK, r.getStatusCode());
    }

    @Test
    void webhookReturns401OnSecurityException() {
        org.mockito.Mockito.doThrow(new SecurityException("invalid"))
                .when(chapaService).handleWebhook(any(), any(), any());
        ResponseEntity<Void> r = controller.chapaWebhook(
                "sig", null, "TRX-1", "{}");
        assertEquals(HttpStatus.UNAUTHORIZED, r.getStatusCode());
    }

    @Test
    void webhookReturns200OnGenericException() {
        org.mockito.Mockito.doThrow(new RuntimeException("boom"))
                .when(chapaService).handleWebhook(any(), any(), any());
        ResponseEntity<Void> r = controller.chapaWebhook(
                "sig", null, "TRX-1", "{}");
        assertEquals(HttpStatus.OK, r.getStatusCode());
    }

    @Test
    void webhookPrefersChapaSignatureHeader() {
        ResponseEntity<Void> r = controller.chapaWebhook(
                "primary", "secondary", "TRX-1", "{}");
        verify(chapaService).handleWebhook(eq("TRX-1"), eq("primary"), eq("{}"));
        assertEquals(HttpStatus.OK, r.getStatusCode());
    }

    @Test
    void webhookFallsBackToXChapaSignature() {
        ResponseEntity<Void> r = controller.chapaWebhook(
                null, "fallback", "TRX-1", "{}");
        verify(chapaService).handleWebhook(eq("TRX-1"), eq("fallback"), eq("{}"));
        assertEquals(HttpStatus.OK, r.getStatusCode());
    }
}
