package com.bicosteve.api_gateway.service;

import com.bicosteve.api_gateway.dto.requests.BetRequest;
import com.bicosteve.api_gateway.dto.response.BetDto;
import com.bicosteve.api_gateway.exceptions.IllegalArgumentException;
import com.bicosteve.api_gateway.mappers.dtomappers.BetDtoMapper;
import com.bicosteve.api_gateway.models.Bet;
import com.bicosteve.api_gateway.repository.BetRepository;
import com.bicosteve.api_gateway.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class BetService{
    private final BetRepository betRepository;
    private final JwtService jwtService;
    private final BetDtoMapper betDtoMapper;

    public BetDto placeBet(BetRequest request){
        // 01. Get profileId from the token sent
        String token = request.getToken();
        if(token == null || token.isEmpty()){
            log.warn("Authorization token is missing");
            throw new IllegalArgumentException("Authorization token is required!");
        }

        Long profileId = this.jwtService.getProfileIdFromToken(token);
        request.setProfileId(profileId.toString());

        // 02. Calculate the total odds
        request.calculateTotalOdds();

        // 02b. Ensure total odds is calculated to 2 decimal places
        if(request.getTotalOdds() != null){
            request.setTotalOdds(request.getTotalOdds().setScale(2, RoundingMode.HALF_UP));
        }

        // 02c. Make sure total odds is not less than 1.20
        if(request.getTotalOdds() == null || request.getTotalOdds().compareTo(new BigDecimal("1.2")) < 0){
            log.warn("total odds must be greater than 1.2");
            throw new java.lang.IllegalArgumentException("Total odds must be greater than 1.2");
        }

        // 03. Calculate the possible win
        BigDecimal stake = BigDecimal.valueOf(request.getStake()).setScale(2,RoundingMode.HALF_UP);
        BigDecimal possibleWin = request.getTotalOdds().multiply(stake).setScale(2, RoundingMode.HALF_UP);

        // 03b. Total Win should not be less than 0
        if(possibleWin.compareTo(BigDecimal.ONE) < 0){
            log.warn("The possible win must be above {}",1);
            throw new IllegalArgumentException("Possible win must be % and above ".formatted(1.0));
        }

        // 04. Try inserting bet on the bets & bet_slips table
        Long betId = this.betRepository.addBet(request,possibleWin.doubleValue());
        if(betId == null || betId < 1){
            log.warn("Bet did not go through");
            throw new IllegalArgumentException("Bet did not go through");
        }

        Bet bet = new Bet();
        bet.setBetId(betId.intValue());
        bet.setProfileId(Integer.valueOf(request.getProfileId()));
        bet.setStake(BigDecimal.valueOf(request.getStake()));
        bet.setPossibleWin(BigDecimal.valueOf(possibleWin.doubleValue()));
        bet.setIsBonus(request.getIsBonus());
        bet.setTotalOdds(request.getTotalOdds());
        

        // 05. Return the result of the operation if success
        return this.betDtoMapper.toDto(bet);
    }
}
