package com.bicosteve.api_gateway.controllers;

import com.bicosteve.api_gateway.dto.requests.BetRequest;
import com.bicosteve.api_gateway.dto.response.BetDto;
import com.bicosteve.api_gateway.service.BetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bet")
@Slf4j
public class BetControllers{
    private final BetService betService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, BetDto>> bet(
            @Valid @RequestBody BetRequest request
        ){

        BetDto bet = this.betService.placeBet(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("details",bet));

    }
}
