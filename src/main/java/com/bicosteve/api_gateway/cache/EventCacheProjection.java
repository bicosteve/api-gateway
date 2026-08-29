package com.bicosteve.api_gateway.cache;

import com.bicosteve.api_gateway.models.Event;
import com.bicosteve.api_gateway.models.Market;
import com.bicosteve.api_gateway.models.Participant;
import com.bicosteve.api_gateway.models.Price;
import com.bicosteve.api_gateway.models.Score;
import com.bicosteve.api_gateway.models.Team;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = false)
public record EventCacheProjection(
        @JsonProperty("schema_version") int schemaVersion,
        Long id,
        @JsonProperty("event_id") String eventId,
        @JsonProperty("event_uuid") String eventUuid,
        @JsonProperty("sport_id") Integer sportId,
        @JsonProperty("event_date") OffsetDateTime eventDate,
        @JsonProperty("season_type") String seasonType,
        @JsonProperty("season_year") Integer seasonYear,
        @JsonProperty("event_name") String eventName,
        @JsonProperty("event_headline") String eventHeadline,
        @JsonProperty("event_status") Integer eventStatus,
        @JsonProperty("tournament") String tournament,
        List<TeamProjection> teams,
        List<MarketProjection> markets,
        ScoreProjection score) {

        public static EventCacheProjection from(Event event) {
            return new EventCacheProjection(
                    1,
                    event.getId(),
                    event.getEventId(),
                    event.getEventUuid(),
                    event.getSportId(),
                    event.getEventDate(),
                    event.getSeasonType(),
                    event.getSeasonYear(),
                    event.getEventName(),
                    event.getEventHeadline(),
                    event.getEventStatus(),
                    event.getTournament(),
                    event.getTeams() == null ? List.of() : event.getTeams().stream().map(TeamProjection::from).toList(),
                    event.getMarkets() == null ? List.of() : event.getMarkets().stream().map(MarketProjection::from).toList(),
                    ScoreProjection.from(event.getScore()));
        }

        public Event toEvent() {
            Event event = Event.builder()
                    .id(id)
                    .eventId(eventId)
                    .eventUuid(eventUuid)
                    .sportId(sportId)
                    .eventDate(eventDate)
                    .seasonType(seasonType)
                    .seasonYear(seasonYear)
                    .eventName(eventName)
                    .eventHeadline(eventHeadline)
                    .eventStatus(eventStatus)
                    .tournament(tournament)
                    .build();

            event.setTeams(teams == null ? new ArrayList<>() : teams.stream().map(TeamProjection::toModel).toList());

            event.setMarkets(markets == null ? new ArrayList<>() : markets.stream().map(MarketProjection::toModel).toList());

            event.setScore(score == null ? null : score.toModel());

            return event;
        }

        record TeamProjection(
                Long id,
                @JsonProperty("team_id") Long teamId,
                @JsonProperty("event_id") String eventId,
                String name,
                String abbreviation,
                @JsonProperty("is_home") boolean isHome,
                @JsonProperty("is_away") boolean isAway,
                @JsonProperty("league_name") String leagueName
        ) {
            static TeamProjection from(Team team) {
                return new TeamProjection(
                        team.getId(),
                        team.getTeamId(),
                        team.getEventId(),
                        team.getName(),
                        team.getAbbreviation(),
                        team.getIsHome() != 0,
                        team.getIsAway() != 0,
                        team.getLeagueName()
                );
            }

            Team toModel() {
                return Team.builder()
                        .id(id)
                        .teamId(teamId)
                        .eventId(eventId)
                        .name(name)
                        .abbreviation(abbreviation)
                        .isHome(isHome ? 1 : 0)
                        .isAway(isAway ? 1 : 0).
                        leagueName(leagueName)
                        .build();
            }
        }

        record MarketProjection(
                @JsonProperty("local_id") Long localId,
                @JsonProperty("market_id") Integer marketId,
                @JsonProperty("market_type_id") Integer marketTypeId,
                @JsonProperty("period_id") int periodId,
                String name,
                @JsonProperty("market_description") String marketDescription,
                @JsonProperty("event_id") String eventId,
                List<ParticipantProjection> participants
        ) {

            static MarketProjection from(Market market) {
                return new MarketProjection(
                        market.getId(),
                        market.getMarketRundownId(),
                        market.getMarketTypeId(),
                        market.getPeriodId() == null ? 0 : market.getPeriodId(),
                        market.getName(),
                        market.getDescription(),
                        market.getEventId(),
                        market.getParticipants() == null ? List.of() : market.getParticipants().
                                stream()
                                .map(ParticipantProjection::from).toList()
                );
            }

            Market toModel() {
                return Market.builder()
                        .id(localId)
                        .marketRundownId(marketId)
                        .marketTypeId(marketTypeId)
                        .periodId(periodId)
                        .name(name)
                        .description(marketDescription)
                        .eventId(eventId)
                        .participants(participants == null ? List.of() : participants
                                .stream()
                                .map(ParticipantProjection::toModel)
                                .toList()).build();
            }
        }

        record ParticipantProjection(
                @JsonProperty("participant_id") Long participantId,
                Long id,
                String type,
                String name,
                @JsonProperty("market_id") Integer marketId,
                List<LineProjection> lines
        ) {
            static ParticipantProjection from(Participant participant) {
                List<Price> prices = participant.getPrices() == null ? List.of() : participant.getPrices();
                return new ParticipantProjection(
                        participant.getParticipantId(),
                        (long) participant.getRundownId(),
                        participant.getType(),
                        participant.getName(),
                        participant.getMarketId() == null ? null : participant.getMarketId().intValue(),
                        List.of(new LineProjection(prices.isEmpty() ? null : prices.getFirst().getLineId(),
                                prices.isEmpty() ? null : prices.getFirst().getHandicapValue(), PriceProjection.from(prices))));
            }

            Participant toModel() {
                List<Price> prices = lines == null ? List.of() : lines.stream().flatMap(line -> line.prices().values().stream())
                        .map(PriceProjection::toModel).toList();
                return Participant.builder().participantId(participantId).rundownId(id == null ? null : id.intValue()).type(type)
                        .name(name).marketId(marketId == null ? null : marketId.longValue()).prices(prices).build();
            }
        }

        record LineProjection(String id, String value, Map<String, PriceProjection> prices) { }

        record PriceProjection(
                @JsonProperty("price_id") Integer priceId,
                @JsonProperty("rundown_id") String rundownId,
                Integer price,
                @JsonProperty("is_main_line") boolean isMainLine,
                BigDecimal odds,
                @JsonProperty("participant_id") Long participantId,
                @JsonProperty("handicap_value") String handicapValue,
                @JsonProperty("line_id") String lineId,
                @JsonProperty("closed_at") OffsetDateTime closedAt
        ) {
            static Map<String, PriceProjection> from(List<Price> prices) {
                java.util.LinkedHashMap<String, PriceProjection> result = new java.util.LinkedHashMap<>();
                for (Price price : prices) {
                    result.put(String.valueOf(price.getPriceId()), new PriceProjection(price.getPriceId(), null,
                            price.getPrice() == null ? null : price.getPrice().intValue(),
                            price.getIsMainLine() != null && price.getIsMainLine() != 0, price.getOdds(),
                            price.getParticipantId() == null ? null : price.getParticipantId().longValue(), price.getHandicapValue(),
                            price.getLineId(), price.getClosedAt()));
                }

                return result;
            }
            Price toModel() {
                return Price.builder().priceId(priceId).price(price == null ? null : BigDecimal.valueOf(price))
                        .isMainLine(isMainLine ? 1 : 0).odds(odds)
                        .participantId(participantId == null ? null : participantId.intValue()).handicapValue(handicapValue)
                        .lineId(lineId).closedAt(closedAt).build();
            }
        }

        record ScoreProjection(
                @JsonProperty("score_id") Long scoreId,
                @JsonProperty("event_id") String eventId,
                @JsonProperty("event_status") Integer eventStatus,
                @JsonProperty("event_status_detail") String eventStatusDetail,
                @JsonProperty("team_id_away") Integer teamIdAway,
                @JsonProperty("team_id_home") Integer teamIdHome,
                @JsonProperty("winner_away") Integer winnerAway,
                @JsonProperty("winner_home") Integer winnerHome,
                @JsonProperty("score_away") Integer scoreAway,
                @JsonProperty("score_home") Integer scoreHome,
                @JsonProperty("game_clock") Integer gameClock,
                @JsonProperty("game_period") Integer gamePeriod) {

            static ScoreProjection from(Score score) {
                return score == null ? null : new ScoreProjection(score.getId(), score.getEventId(), score.getEventStatus(),
                        score.getEventStatusDetail(), score.getTeamIdAway(), score.getTeamIdHome(), score.getWinnerAway(),
                        score.getWinnerHome(), score.getScoreAway(), score.getScoreHome(), score.getGameClock(), score.getGamePeriod());
            }

            Score toModel() {
                return Score.builder().id(scoreId).eventId(eventId).eventStatus(eventStatus).eventStatusDetail(eventStatusDetail)
                        .teamIdAway(teamIdAway).teamIdHome(teamIdHome).winnerAway(winnerAway).winnerHome(winnerHome)
                        .scoreAway(scoreAway).scoreHome(scoreHome).gameClock(gameClock).gamePeriod(gamePeriod).build();
            }
        }
}
