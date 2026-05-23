package com.bicosteve.api_gateway.mappers.dtomappers;


import com.bicosteve.api_gateway.dto.response.*;
import com.bicosteve.api_gateway.models.Event;
import com.bicosteve.api_gateway.models.Price;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class EventDtoMapper{

    public EventResponse toDto(Event event){
        if(event == null) return null;

        // 01. Set event's details
        EventResponse dto = new EventResponse();

        dto.setEventId(event.getEventId());
        dto.setSportId(event.getSportId());
        dto.setEventDate(event.getEventDate());
        dto.setSeasonType(event.getSeasonType());
        dto.setSeasonYear(event.getSeasonYear());
        dto.setEventName(event.getEventName());
        dto.setEventStatus(event.getEventStatus());


        // 02. Check and set teams for an event
        if(event.getTeams() != null){
            List<TeamResponse> teamsDto = event.getTeams().stream()
                    .map(team -> {
                        TeamResponse teamResponse = new TeamResponse();
                        teamResponse.setId(team.getId());
                        teamResponse.setTeamId(team.getTeamId());
                        teamResponse.setName(team.getName());
                        teamResponse.setIsHome(team.getIsHome());
                        teamResponse.setIsAway(team.getIsAway());
                        teamResponse.setLeagueName(team.getLeagueName());

                        return teamResponse;
                    }).toList();

            dto.setTeams(teamsDto);
        }

        // 03. Check and set markets for an event
        if(event.getMarkets() != null){
            List<MarketResponse> marketsDto = event.getMarkets().stream()
                    .map(market -> {
                        MarketResponse marketResponse = new MarketResponse();

                        marketResponse.setMarketRundownId(market.getMarketRundownId());
                        marketResponse.setMarketTypeId(market.getMarketTypeId());
                        marketResponse.setPeriodId(market.getPeriodId());
                        marketResponse.setName(market.getName());


                        // 04. Check and set participants for market
                        if(market.getParticipants() != null){
                            List<ParticipantResponse> participantsDto = market.getParticipants().stream()
                                    .map(participant -> {
                                        ParticipantResponse participantResponse = new ParticipantResponse();

                                        participantResponse.setParticipantId(participant.getParticipantId());
                                        participantResponse.setRundownId(participant.getRundownId());
                                        participantResponse.setType(participant.getType());
                                        participantResponse.setName(participant.getName());
                                        participantResponse.setMarketId(participant.getMarketId());

                                        // 05. Check & set price for participants
                                        if(participant.getPrices() != null && !participant.getPrices().isEmpty()){

                                            Price price = participant.getPrices().get(0);

                                            PriceResponse priceResponse = new PriceResponse();
                                            priceResponse.setPriceId(price.getPriceId());
                                            // priceResponse.setRundownId(price.getRundownId());
                                            priceResponse.setOdds(price.getOdds());
                                            priceResponse.setParticipantId(price.getParticipantId());
                                            priceResponse.setHandicapValue(price.getHandicapValue());
                                            priceResponse.setLineId(price.getLineId());
                                            priceResponse.setClosedAt(price.getClosedAt());

                                            participantResponse.setPrices(List.of(priceResponse));

                                        } else {
                                            participantResponse.setPrices(new ArrayList<>());
                                        }

                                        return participantResponse;

                                    }).toList();


                            marketResponse.setParticipants(participantsDto);
                        }

                        return marketResponse;


                    }).toList();

            dto.setMarkets(marketsDto);
        }

        // 06. Check & set scores for the event
        if(event.getScore() != null){
            ScoreResponse scoreResponse = new ScoreResponse();

            scoreResponse.setId(event.getScore().getId());
            scoreResponse.setEventStatus(event.getScore().getEventStatus());
            scoreResponse.setScoreAway(event.getScore().getScoreAway());
            scoreResponse.setScoreHome(event.getScore().getScoreHome());
            scoreResponse.setGameClock(event.getScore().getGameClock());
            scoreResponse.setGamePeriod(event.getScore().getGamePeriod());

            dto.setScore(scoreResponse);
        }

        return dto;
    }
}
