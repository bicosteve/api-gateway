package com.bicosteve.api_gateway.mappers.dtomappers;

import com.bicosteve.api_gateway.dto.response.BetResponse;
import com.bicosteve.api_gateway.dto.response.SlipResponse;
import com.bicosteve.api_gateway.models.Bet;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BetDtoMapper{

    public BetResponse toDto(Bet bet){
        if(bet == null) return null;

        // Set bet details

        BetResponse dto = new BetResponse();

        dto.setBetId(bet.getBetId());
        dto.setProfileId(bet.getProfileId());
        dto.setStake(bet.getStake());
        dto.setIsBonus(bet.getIsBonus());
        dto.setStatus(bet.getStatus() != null ? bet.getStatus() : 0);
        dto.setStake(bet.getStake());
        dto.setTotalOdds(bet.getTotalOdds());
        dto.setPossibleWin(bet.getPossibleWin());
        dto.setCreatedAt(bet.getCreatedAt());

        if(bet.getSlips() != null){
            List<SlipResponse> slipsDto = bet.getSlips().stream()
                    .map(slip -> {
                        SlipResponse slipResponse = new SlipResponse();

                        slipResponse.setBetSlipId(Long.valueOf(slip.getBetSlipId()));
                        slipResponse.setBetId(Long.valueOf(slip.getBetId()));
                        slipResponse.setEventId(slip.getEventId());
                        slipResponse.setSportId(slip.getSportId());
                        slipResponse.setTeamId(slip.getTeamId());
                        slipResponse.setMarketId(slip.getMarketId());
                        slipResponse.setMarketName(slip.getMarketName());
                        slipResponse.setParticipantName(slip.getParticipantName());
                        slipResponse.setOdds(slip.getOdds());
                        slipResponse.setSpecialBetValue(slip.getSpecialBetValue());
                        slipResponse.setStatus(slip.getStatus());
                        slipResponse.setCreatedAt(slip.getCreatedAt());
                        slipResponse.setUpdatedAt(slip.getUpdatedAt());

                        return slipResponse;

                    }).toList();

            dto.setSlips(slipsDto);

        }

        return dto;
    }
}
