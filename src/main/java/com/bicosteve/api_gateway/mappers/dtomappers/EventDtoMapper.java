package com.bicosteve.api_gateway.mappers.dtomappers;


import com.bicosteve.api_gateway.dto.response.*;
import com.bicosteve.api_gateway.models.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class EventDtoMapper{

    public EventDto toDto(Event event){
        if(event == null) return null;

        // 01. Set event's details
        EventDto dto = new EventDto();

        dto.setEventId(event.getEventId());
        dto.setSportId(event.getSportId());
        dto.setEventDate(event.getEventDate());
        dto.setSeasonType(event.getSeasonType());
        dto.setSeasonYear(event.getSeasonYear());
        dto.setEventName(event.getEventName());
        dto.setEventStatus(event.getEventStatus());


        // 02. Check and set teams for an event
        if(event.getTeams() != null){
            List<TeamDto> teamsDto = event.getTeams().stream()
                    .map(team -> {
                        TeamDto teamDto = new TeamDto();
                        teamDto.setId(team.getId());
                        teamDto.setTeamId(team.getTeamId());
                        teamDto.setName(team.getName());
                        teamDto.setIsHome(team.getIsHome());
                        teamDto.setIsAway(team.getIsAway());
                        teamDto.setLeagueName(team.getLeagueName());

                        return teamDto;
                    }).toList();

            dto.setTeams(teamsDto);
        }

        // 03. Check and set markets for an event
        if(event.getMarkets() != null){
            List<MarketDto> marketsDto = event.getMarkets().stream()
                    .map(market -> {
                        MarketDto marketDto = new MarketDto();

                        marketDto.setMarketRundownId(market.getMarketRundownId());
                        marketDto.setMarketTypeId(market.getMarketTypeId());
                        marketDto.setPeriodId(market.getPeriodId());
                        marketDto.setName(market.getName());


                        // 04. Check and set participants for market
                        if(market.getParticipants() != null){
                            List<ParticipantDto> participantsDto = market.getParticipants().stream()
                                    .map(participant -> {
                                        ParticipantDto participantDto = new ParticipantDto();

                                        participantDto.setParticipantId(participant.getParticipantId());
                                        participantDto.setRundownId(participant.getRundownId());
                                        participantDto.setType(participant.getType());
                                        participantDto.setName(participant.getName());
                                        participantDto.setMarketId(participant.getMarketId());

                                        // 05. Check & set prices for participants
                                        if(participant.getPrices() != null){
                                            List<PriceDto> pricesDto = participant.getPrices().stream()
                                                    .map( price -> {

                                                        PriceDto priceDto = new PriceDto();

                                                        priceDto.setPriceId(price.getPriceId());
                                                        priceDto.setRundownId(price.getRundownId());
                                                        priceDto.setOdds(price.getOdds());
                                                        priceDto.setParticipantId(price.getParticipantId());
                                                        priceDto.setHandicapValue(price.getHandicapValue());
                                                        priceDto.setLineId(price.getLineId());
                                                        priceDto.setClosedAt(price.getClosedAt());

                                                        return priceDto;

                                                    }).toList();

                                            participantDto.setPrices(pricesDto);

                                        }


                                        return participantDto;

                                    }).toList();


                            marketDto.setParticipants(participantsDto);
                        }

                        return marketDto;


                    }).toList();

            dto.setMarkets(marketsDto);
        }

        // 06. Check & set scores for the event
        if(event.getScore() != null){
            ScoreDto scoreDto = new ScoreDto();

            scoreDto.setId(event.getScore().getId());
            scoreDto.setEventStatus(event.getScore().getEventStatus());
            scoreDto.setScoreAway(event.getScore().getScoreAway());
            scoreDto.setScoreHome(event.getScore().getScoreHome());
            scoreDto.setGameClock(event.getScore().getGameClock());
            scoreDto.setGamePeriod(event.getScore().getGamePeriod());

            dto.setScore(scoreDto);
        }

        return dto;
    }
}
