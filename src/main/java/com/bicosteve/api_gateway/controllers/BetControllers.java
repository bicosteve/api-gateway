package com.bicosteve.api_gateway.controllers;

import com.bicosteve.api_gateway.dto.requests.BetRequest;
import com.bicosteve.api_gateway.dto.response.BetDto;
import com.bicosteve.api_gateway.service.BetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bet")
@Slf4j
public class BetControllers{
    private final BetService betService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, BetDto>> bet(
            @Valid @RequestBody BetRequest request,
            Authentication authentication
        ){

        BetDto bet = this.betService.placeBet(request, authentication);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("details",bet));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getBets(
           @RequestParam(defaultValue = "all") String filter,
           @RequestParam(defaultValue = "1") int page,
           @RequestParam(defaultValue = "10") int size,
           Authentication authentication
     ){

        String[] validFilters = {"all","day","week","month"};
        if(Arrays.asList(validFilters).contains(filter.toLowerCase())){
            List<BetDto> bets = this.betService.getBets(filter,page,size,authentication);
            return ResponseEntity.status(HttpStatus.OK).body(bets);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","Filter provided does not exist"));
     }

     @GetMapping("/{betId}")
    public ResponseEntity<BetDto> getOneBet(@PathVariable Long betId, Authentication authentication){
        return ResponseEntity.status(HttpStatus.OK).body(this.betService.getBet(betId,authentication));
     }
}
