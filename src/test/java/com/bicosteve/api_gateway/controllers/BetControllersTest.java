package com.bicosteve.api_gateway.controllers;

import com.bicosteve.api_gateway.dto.requests.BetRequest;
import com.bicosteve.api_gateway.dto.requests.SlipRequest;
import com.bicosteve.api_gateway.dto.response.BetResponse;
import com.bicosteve.api_gateway.dto.response.PageResponse;
import com.bicosteve.api_gateway.security.CustomUserDetails;
import com.bicosteve.api_gateway.service.BetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BetControllersTest {

    private BetService betService;
    private BetControllers controller;

    @BeforeEach
    void setUp() {
        betService = mock(BetService.class);
        controller = new BetControllers(betService);
    }

    private Authentication auth() {
        CustomUserDetails cud = new CustomUserDetails(7L, "254701234567",
                1, 1, 0, "x", List.of());
        return new UsernamePasswordAuthenticationToken(cud, null, List.of());
    }

    @Test
    void betReturns201() {
        BetRequest req = new BetRequest();
        req.setStake(10.0);
        req.setIsBonus(0);
        req.setSlips(List.of(new SlipRequest("e1", 19, 1, 1, "moneyline", "Chelsea", 2.0, "")));

        BetResponse response = BetResponse.builder()
                .betId(99L).profileId(7L)
                .stake(BigDecimal.TEN)
                .totalOdds(BigDecimal.valueOf(2.0))
                .possibleWin(BigDecimal.valueOf(20))
                .build();
        when(betService.placeBet(eq(req), any(Authentication.class))).thenReturn(response);

        ResponseEntity<BetResponse> r = controller.bet(req, auth());

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        assertEquals(99L, r.getBody().getBetId());
    }

    @Test
    void getOneBetReturnsOk() {
        BetResponse response = BetResponse.builder().betId(50L).build();
        when(betService.getBet(eq(50L), any(Authentication.class))).thenReturn(response);

        ResponseEntity<BetResponse> r = controller.getOneBet(50L, auth());
        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(50L, r.getBody().getBetId());
    }

    @Test
    void getBetsReturnsPage() {
        PageResponse<BetResponse> page = PageResponse.<BetResponse>builder()
                .data(List.of(BetResponse.builder().betId(1L).build()))
                .page(0).limit(10).hasNext(false).hasPrevious(false)
                .build();
        when(betService.getBets(eq("all"), eq(0), eq(10), any(Authentication.class)))
                .thenReturn(page);

        ResponseEntity<PageResponse<BetResponse>> r = controller.getBets("all", 0, 10, auth());

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1, r.getBody().getData().size());
    }
}
