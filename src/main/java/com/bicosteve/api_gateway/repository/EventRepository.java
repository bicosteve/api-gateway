package com.bicosteve.api_gateway.repository;


import com.bicosteve.api_gateway.models.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventRepository{
    private final JdbcTemplate jdbcTemplate;

    // STEP 01: Fetch Events
    public List<Event> fetchEvents(int limit, int offset){
        String sql = """
                SELECT
                    e.id,
                    e.event_id,
                    e.event_uuid,
                    e.sport_id,
                    e.event_date,
                    e.season_type,
                    e.season_year,
                    e.event_name,
                    e.event_status,
                    t.id AS teams_id,
                    t.team_id,
                    t.event_id AS teams_event_id,
                    t.name AS team_name,
                    t.abbreviation,
                    t.is_home,
                    t.is_away,
                    t.league_name,
                    m.id AS markets_id,
                    m.market_rundown_id,
                    m.market_type_id,
                    m.period_id,
                    m.name AS market_name,
                    m.description,
                    m.event_id,
                    s.id AS score_id,
                    s.event_id,
                    s.event_status,
                    s.event_status_detail,
                    s.team_id_away,
                    s.team_id_home,
                    s.winner_home,
                    s.winner_away,
                    s.score_away,
                    s.score_home,
                    s.game_clock,
                    s.game_period,
                    p.participant_id,
                    p.rundown_id,
                    p.type,
                    p.name AS participant_name,
                    p.market_id AS participant_market_id,
                    pr.price_id,
                    pr.rundown_id AS price_rundown_id,
                    pr.price,
                    pr.is_main_line,
                    pr.odds,
                    pr.participant_id,
                    pr.handicap_value,
                    pr.line_id,
                    pr.closed_at AS price_closed_at
                FROM (
                    SELECT * FROM rundown_event
                    ORDER BY updated_at DESC
                    LIMIT ? OFFSET ?
                ) e
                LEFT JOIN teams t ON t.event_id = e.event_id
                LEFT JOIN markets m ON e.event_id = m.event_id
                LEFT JOIN scores s ON s.event_id = e.event_id
                LEFT JOIN participants p ON m.id = p.market_id
                LEFT JOIN prices pr ON pr.participant_id = p.participant_id
                ORDER BY e.updated_at DESC
             """;

        log.info(
                "EventRepository::Get events query {}, limit {}, offset {}",
                sql,
                limit,
                offset
                );

        return this.jdbcTemplate.query(
                sql,
                ps -> {
                    ps.setInt(1,limit);
                    ps.setInt(2,offset);
                    },
                new EventExtractor()
        );
    }

    private static class EventExtractor implements ResultSetExtractor<List<Event>>{
        @Override
        public List<Event> extractData(ResultSet rs) throws SQLException{

            Map<Long,Event> eventMap = new LinkedHashMap<>();

            while(rs.next()){
                Long id = rs.getLong("id");
                String eventId = rs.getString("event_id");
                String eventUuid = rs.getString("event_uuid");
                Integer sportId = rs.getInt("sport_id");
                java.sql.Timestamp eventTimestamp = rs.getTimestamp("event_date");
                LocalDateTime eventDate = (eventTimestamp != null) ? eventTimestamp.toLocalDateTime() : null;
                String seasonType = rs.getString("season_type");
                Integer seasonYear = rs.getInt("season_year");
                String eventName = rs.getString("event_name");
                Integer eventStatus = rs.getInt("event_status");

                Event event = eventMap.computeIfAbsent(id,key -> {
                    Event e = new Event();
                    e.setId(key);
                    e.setEventId(eventId);
                    e.setEventUuid(eventUuid);
                    e.setSportId(sportId);
                    e.setEventDate(eventDate);
                    e.setSeasonType(seasonType);
                    e.setSeasonYear(seasonYear);
                    e.setEventName(eventName);
                    e.setEventStatus(eventStatus);

                    e.setTeams(new ArrayList<>());
                    e.setMarkets(new ArrayList<>());

                    return e;
                });

                Long marketId = rs.getLong("markets_id");
                Integer marketRundownId = rs.getInt("market_rundown_id");
                Integer marketTypeId = rs.getInt("market_type_id");
                Integer periodId = rs.getInt("period_id");
                String name = rs.getString("market_name");
                String description = rs.getString("description");
                String marketEventId = rs.getString("event_id");

                if(marketId != 0){
                    Market market = event.getMarkets().stream()
                            .filter(m -> m.getId().equals(marketId))
                            .findFirst()
                            .orElseGet(() -> {
                                Market m = new Market();
                                m.setId(marketId);
                                m.setMarketRundownId(marketRundownId);
                                m.setMarketTypeId(marketTypeId);
                                m.setPeriodId(periodId);
                                m.setName(name);
                                m.setDescription(description);
                                m.setEventId(marketEventId);

                                m.setParticipants(new ArrayList<>()); // Initializing participants

                                event.getMarkets().add(m);

                                return m;
                            });


                    // Getting participants
                    Long participantId = rs.getLong("participant_id");
                    Integer participantRundownId = rs.getInt("rundown_id");
                    String participantType = rs.getString("type");
                    String participantName = rs.getString("participant_name");
                    Long participantMarketId = rs.getLong("participant_market_id");

                    // Getting prices
                    Integer priceId = rs.getInt("price_id");
                    Integer priceRundownId = rs.getInt("price_rundown_id");
                    BigDecimal price = rs.getBigDecimal("price");
                    Integer isMainLine = rs.getInt("is_main_line");
                    BigDecimal priceOdd = rs.getBigDecimal("odds");
                    Integer priceParticipantId = rs.getInt("participant_id");
                    String handicapValue = rs.getString("handicap_value");
                    String lineId = rs.getString("line_id");
                    java.sql.Timestamp priceTimestamp = rs.getTimestamp("price_closed_at");
                    LocalDateTime priceClosedAt = (priceTimestamp != null) ? priceTimestamp.toLocalDateTime() : null;


                    // Set participant
                    if(participantId != 0){
                        Participant participant = market.getParticipants().stream()
                                .filter(p -> p.getParticipantId().equals(participantId))
                                .findFirst()
                                .orElseGet(() -> {
                                    Participant p = new Participant();
                                    p.setParticipantId(participantId);
                                    p.setRundownId(participantRundownId);
                                    p.setType(participantType);
                                    p.setName(participantName);
                                    p.setMarketId(participantMarketId);

                                    p.setPrices(new ArrayList<>()); // initialize prices as empty list

                                    market.getParticipants().add(p);

                                    return p;
                                });

                        // Set prices
                        if(price != null){

                            Price p = new Price();

                            p.setPriceId(priceId);
                            p.setRundownId(priceRundownId);
                            p.setPrice(price);
                            p.setIsMainLine(isMainLine);
                            p.setOdds(priceOdd);
                            p.setParticipantId(priceParticipantId);
                            p.setHandicapValue(handicapValue);
                            p.setLineId(lineId);
                            p.setClosedAt(priceClosedAt);

                            participant.getPrices().add(p);
                        }
                    }
                }

                // Score
                Long scoreId = rs.getObject("score_id", Long.class);
                String scoreEventId = rs.getString("event_id");
                Integer scoreEventStatus = rs.getInt("event_status");
                String eventStatusDetail = rs.getString("event_status_detail");
                Integer teamIdAway = rs.getInt("team_id_away");
                Integer teamIdHome = rs.getInt("team_id_home");
                Integer winnerHome = rs.getInt("winner_home");
                Integer winnerAway = rs.getInt("winner_away");
                Integer scoreHome = rs.getInt("score_home");
                Integer scoreAway = rs.getInt("score_away");
                Integer gameClock = rs.getInt("game_clock");
                Integer gamePeriod = rs.getInt("game_period");

                if(scoreId != null){
                    Score score = new Score();
                    score.setId(scoreId);
                    score.setEventId(scoreEventId);
                    score.setEventStatus(scoreEventStatus);
                    score.setEventStatusDetail(eventStatusDetail);
                    score.setTeamIdAway(teamIdAway);
                    score.setTeamIdHome(teamIdHome);
                    score.setWinnerHome(winnerHome);
                    score.setWinnerAway(winnerAway);
                    score.setScoreHome(scoreHome);
                    score.setScoreAway(scoreAway);
                    score.setGameClock(gameClock);
                    score.setGamePeriod(gamePeriod);

                    event.setScore(score);
                }


                // Team
                Long teamsId = rs.getLong("teams_id");
                Long teamId = rs.getLong("team_id");
                String teamsEventId = rs.getString("teams_event_id");
                String teamsName = rs.getString("team_name");
                String teamsAbbreviation = rs.getString("abbreviation");
                int isHome = rs.getInt("is_home");
                int isAway = rs.getInt("is_away");
                String teamsLeague = rs.getString("league_name");

                if(teamId != null && teamId != 0){

                    boolean exists = event.getTeams()
                            .stream()
                            .anyMatch(team -> team.getTeamId().equals(teamId));

                    // Avoids the duplicate of teams in the teams list
                    if(!exists){
                        Team t = new Team();

                        t.setId(teamsId);
                        t.setTeamId(teamId);
                        t.setEventId(teamsEventId);
                        t.setName(teamsName);
                        t.setAbbreviation(teamsAbbreviation);
                        t.setIsHome(isHome);
                        t.setIsAway(isAway);
                        t.setLeagueName(teamsLeague);

                        event.getTeams().add(t);
                    }
                }
            }

            log.info("EventRepository::Events {} ",eventMap);

            return new ArrayList<>(eventMap.values());
        }
    }


}
