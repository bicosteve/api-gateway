package com.bicosteve.api_gateway.mappers.dtomappers;

import com.bicosteve.api_gateway.dto.response.BetDto;
import com.bicosteve.api_gateway.dto.response.SlipDto;
import com.bicosteve.api_gateway.models.Bet;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BetDtoMapper{

    public BetDto toDto(Bet bet){
        if(bet == null) return null;

        // Set bet details

        BetDto dto = new BetDto();

        dto.setBetId(bet.getBetId());
        dto.setProfiledId(bet.getProfileId());
        dto.setStake(bet.getStake());
        dto.setIsBonus(bet.getIsBonus());
        dto.setStatus(bet.getStatus() != null ? bet.getStatus() : 0);
        dto.setStake(bet.getStake());
        dto.setTotalOdds(bet.getTotalOdds());
        dto.setPossibleWin(bet.getPossibleWin());
        dto.setCreatedAt(bet.getCreated_at());

        if(bet.getSlips() != null){
            List<SlipDto> slipsDto = bet.getSlips().stream()
                    .map(slip -> {
                        SlipDto slipDto = new SlipDto();

                        slipDto.setBetSlipId(Long.valueOf(slip.getBetSlipId()));
                        slipDto.setBetId(Long.valueOf(slip.getBetId()));
                        slipDto.setEventId(slip.getEventId());
                        slipDto.setSportId(slip.getSportId());
                        slipDto.setTeamId(slip.getTeamId());
                        slipDto.setMarketId(slip.getMarketId());
                        slipDto.setMarketName(slip.getMarketName());
                        slipDto.setParticipantName(slip.getParticipantName());
                        slipDto.setOdds(slip.getOdds());

                        return slipDto;

                    }).toList();

            dto.setSlips(slipsDto);

        }

        return dto;
    }
}
