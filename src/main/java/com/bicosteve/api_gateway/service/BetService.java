package com.bicosteve.api_gateway.service;

import com.bicosteve.api_gateway.dto.requests.BetRequest;
import com.bicosteve.api_gateway.dto.response.BetDto;
import com.bicosteve.api_gateway.exceptions.IllegalArgumentException;
import com.bicosteve.api_gateway.repository.BetRepository;
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

    public BetDto placeBet(BetRequest request){
        // 01. Calculate the total odds
        request.calculateTotalOdds();

        // 02. Ensure total odds is calculated to 2 decimal places
        if(request.getTotalOdds() != null){
            request.setTotalOdds(request.getTotalOdds().setScale(2, RoundingMode.HALF_UP));
        }

        // 03. Make sure total odds is not less than 1.20
        if(request.getTotalOdds() == null || request.getTotalOdds().compareTo(new BigDecimal("1.2")) < 0){
            log.warn("total odds must be greater than 1.2");
            throw new java.lang.IllegalArgumentException("Total odds must be greater than 1.2");
        }

        BigDecimal stake = BigDecimal.valueOf(request.getStake()).setScale(2,RoundingMode.HALF_UP);
        BigDecimal possibleWin = request.getTotalOdds().multiply(stake).setScale(2, RoundingMode.HALF_UP);

        // 04. Total Win should not be less than 0
        if(possibleWin.compareTo(BigDecimal.ONE) < 0){
            log.warn("The possible win must be above {}",1);
            throw new IllegalArgumentException("Possible win must be % and above ".formatted(1.0));
        }

        // 05. Try to add bet in the bets table
        Long betId = this.betRepository.addBet(request,possibleWin.doubleValue());
        if(betId == null || betId < 1){
            log.warn("Bet did not go through");
            throw new IllegalArgumentException("Bet did not go through");
        }

        // 06. Return the result of the operation
        return BetDto.builder()
                .betId(betId.intValue())
                .profiledId(request.getProfileId())
                .stake(request.getStake())
                .possibleWin(possibleWin.doubleValue())
                .isBonus(request.getIsBonus())
                .build();
    }
}
