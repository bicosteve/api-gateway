package com.bicosteve.api_gateway.repository;


import com.bicosteve.api_gateway.models.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

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


    // STEP 02. Fetch a single event
    public Event fetchOneEvent(String eventId){
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
                    e.event_headline,
                    e.event_status,
                    t.id AS teams_id,
                    t.team_id,
                    t.event_id AS teams_event_id,
                    t.name AS teams_name,
                    t.abbreviation,
                    t.is_home,
                    t.is_away,
                    t.league_name,
                    m.id AS markets_id,
                    m.market_rundown_id,
                    m.market_type_id,
                    m.period_id,
                    m.name AS markets_name,
                    m.description,
                    m.event_id AS market_event_id,
                    s.id AS scores_id,
                    s.event_id AS scores_event_id,
                    s.event_status AS scores_event_status,
                    s.event_status_detail AS scores_event_status_detail,
                    s.team_id_away,
                    s.team_id_home,
                    s.winner_home,
                    s.winner_away,
                    s.score_away,
                    s.score_home,
                    s.game_clock,
                    s.game_period,
                    p.participant_id,
                    p.rundown_id AS participant_rundown_id,
                    p.type,
                    p.name AS participant_name,
                    p.market_id AS participant_market_id,
                    pr.price_id,
                    pr.rundown_id AS price_rundown_id,
                    pr.odds,
                    pr.participant_id AS price_participant_id,
                    pr.handicap_value,
                    pr.line_id,
                    pr.closed_at AS price_closed_at
                FROM rundown_event e
                LEFT JOIN teams t ON t.event_id = e.event_id
                LEFT JOIN markets m ON m.event_id = e.event_id
                LEFT JOIN scores s ON s.event_id = e.event_id
                LEFT JOIN participants p ON p.market_id = m.id
                LEFT JOIN prices pr ON pr.participant_id = p.participant_id
                WHERE e.event_id = ?
             """;

        return this.jdbcTemplate.query(
                sql,
                ps -> ps.setString(1, eventId),
                new OneEventExtractor()
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

                if(teamId != 0){

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
            
            return new ArrayList<>(eventMap.values());
        }
    }

    private static class OneEventExtractor implements ResultSetExtractor<Event>{

        @Override
        public Event extractData(ResultSet rs) throws SQLException, DataAccessException{
            Event  event = null;

            Map<Long,Team> teamMap = new HashMap<>();
            Map<Long, Market> marketMap = new HashMap<>();
            Map<Long, Participant> participantMap = new HashMap<>();

            // 01. While we have items in the result set,
            // if event is null, initialize an event then set its properties
            while(rs.next()){

                Timestamp eventDateTs = rs.getTimestamp("event_date");
                Timestamp priceClosedAtTs = rs.getTimestamp("price_closed_at");

                if(event == null){
                    event = new Event();
                    event.setId(rs.getLong("id"));
                    event.setEventId(rs.getString("event_id"));
                    event.setEventUuid(rs.getString("event_uuid"));
                    event.setSportId(rs.getInt("sport_id"));

                    if(eventDateTs != null){
                        event.setEventDate(eventDateTs.toLocalDateTime());
                    }

                    event.setSeasonType(rs.getString("season_type"));
                    event.setSeasonYear(rs.getInt("season_year"));
                    event.setEventName(rs.getString("event_name"));
                    event.setEventHeadline(rs.getString("event_headline"));
                    event.setEventStatus(rs.getInt("event_status"));

                    // Compose teams and markets
                    event.setTeams(new ArrayList<>());
                    event.setMarkets(new ArrayList<>());


                    // 02. Set Score for the event
                    Long scoreId = rs.getLong("scores_id");
                    if(scoreId != 0){
                        Score score = new Score();
                        score.setId(scoreId);
                        score.setEventId(rs.getString("scores_event_id"));
                        score.setScoreHome(rs.getInt("score_home"));
                        score.setScoreAway(rs.getInt("score_away"));
                        score.setEventStatus(rs.getInt("scores_event_status"));
                        score.setGameClock(rs.getInt("game_clock"));
                        score.setGamePeriod(rs.getInt("game_period"));

                        event.setScore(score);
                    }
                }

                // 03. Set Teams for the event
                Long teamsId = rs.getLong("teams_id");
                Long teamId = rs.getLong("team_id");
                String teamName = rs.getString("teams_name");
                Integer isHome = rs.getInt("is_home");
                Integer isAway = rs.getInt("is_away");
                String leaguesName = rs.getString("league_name");
                String eventId = event.getEventId();

                if(teamsId != 0){
                    Team team = teamMap.computeIfAbsent(teamsId, id -> {
                        Team t = new Team();

                        t.setId(teamsId);
                        t.setTeamId(teamId);
                        t.setName(teamName);
                        t.setIsHome(isHome);
                        t.setIsAway(isAway);
                        t.setLeagueName(leaguesName);
                        t.setEventId(eventId);

                        return t;
                    });

                   if(!event.getTeams().contains(team)){
                       event.getTeams().add(team);
                   }

                }

                // 04. Set Markets for the event
                Long marketId = rs.getLong("markets_id");
                Integer marketRundownId = rs.getInt("market_rundown_id");
                Integer marketTypeId = rs.getInt("market_type_id");
                Integer marketPeriodId = rs.getInt("period_id");
                String marketName = rs.getString("markets_name");
                String marketDescription = rs.getString("description");
                String marketEventId = rs.getString("market_event_id");


                if(marketId != 0){
                    Market market = marketMap.computeIfAbsent(marketId,id -> {

                        Market m = new Market();

                        m.setId(marketId);
                        m.setMarketRundownId(marketRundownId);
                        m.setMarketTypeId(marketTypeId);
                        m.setPeriodId(marketPeriodId);
                        m.setName(marketName);
                        m.setDescription(marketDescription);
                        m.setEventId(marketEventId);

                        m.setParticipants(new ArrayList<>());

                        return m;

                    });

                    // Check if event already has the market,
                    // if not, set the market

                    if(!event.getMarkets().contains(market)){
                        event.getMarkets().add(market);
                    }


                    // 05. Set markets Participants
                    Long participantId = rs.getLong("participant_id");
                    Integer participantRundownId = rs.getInt("participant_rundown_id");
                    String participantType = rs.getString("type");
                    String participantName = rs.getString("participant_name");
                    Long participantMarketId = rs.getLong("participant_market_id");

                    if(participantId != 0){
                        Participant participant = participantMap
                                .computeIfAbsent(participantId,id -> {
                                    Participant p = new Participant();
                                    p.setParticipantId(participantId);
                                    p.setRundownId(participantRundownId);
                                    p.setType(participantType);
                                    p.setName(participantName);
                                    p.setMarketId(participantMarketId);

                                    p.setPrices(new ArrayList<>());

                                    return p;
                        });

                        if(!market.getParticipants().contains(participant)){
                            market.getParticipants().add(participant);
                        }

                        // Set Price for participant
                        Integer priceId = rs.getInt("price_id");
                        Integer priceRundownId = rs.getInt("price_rundown_id");
                        BigDecimal priceOdds = rs.getBigDecimal("odds");
                        String priceHandicapValue = rs.getString("handicap_value");

                        if(priceId != 0){

                            Price price = new Price();

                            price.setPriceId(priceId);
                            price.setRundownId(priceRundownId);
                            price.setOdds(priceOdds);
                            price.setHandicapValue(priceHandicapValue);

                            if(priceClosedAtTs != null){
                                price.setClosedAt(priceClosedAtTs.toLocalDateTime());
                            }

                            participant.getPrices().add(price);
                        }
                    }
                }

            }
            return event;
        }
    }


}
