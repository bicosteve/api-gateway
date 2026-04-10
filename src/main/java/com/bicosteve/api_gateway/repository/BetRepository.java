package com.bicosteve.api_gateway.repository;

import com.bicosteve.api_gateway.dto.requests.BetRequest;
import com.bicosteve.api_gateway.dto.requests.SlipRequest;
import com.bicosteve.api_gateway.exceptions.IllegalArgumentException;
import com.bicosteve.api_gateway.mappers.rowmappers.BetRowMapper;
import com.bicosteve.api_gateway.mappers.rowmappers.SlipRowMapper;
import com.bicosteve.api_gateway.models.Bet;
import com.bicosteve.api_gateway.models.Slip;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class BetRepository{
    private final JdbcTemplate jdbcTemplate;
    private final BetRowMapper betRowMapper;
    private final SlipRowMapper slipRowMapper;


    @Transactional
    public Long addBet(BetRequest request,Double possibleWin){
        Long betId = insertBet(request,possibleWin);
        insertSlip(request.getSlips(),betId);

        return betId;
    }

    private Long insertBet(BetRequest request, Double possibleWin){
        String sql = """
                INSERT INTO
                    bets(profile_id, stake, is_bonus, status, total_odds, possible_win)
                VALUES(?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int affectedRows = this.jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"bet_id"});

            ps.setString(1, request.getProfileId());
            ps.setBigDecimal(2,BigDecimal.valueOf(request.getStake()));
            ps.setInt(3,request.getIsBonus());
            ps.setInt(4,0);
            ps.setBigDecimal(5,request.getTotalOdds());
            ps.setBigDecimal(6,BigDecimal.valueOf(possibleWin));

            return ps;

        }, keyHolder);

        if(affectedRows == 0 || keyHolder.getKey() == null){
            throw new RuntimeException("Failed to insert bet");
        }

        // Return the betId as long
        return keyHolder.getKey().longValue();
    }

    private void insertSlip(List<SlipRequest> slips, Long betId){
        String sql = """
                INSERT INTO
                    bet_slips(bet_id,event_id,sport_id,team_id,market_id,market_name,participant_name,odds)
                VALUES(?, ?, ?, ?, ?, ?, ?, ?)
                """;

        this.jdbcTemplate.batchUpdate(
                sql,
                slips,
                slips.size(),
                (PreparedStatement ps, SlipRequest slip)->{
                    ps.setLong(1,betId);
                    ps.setString(2, slip.getEventId());
                    ps.setInt(3, slip.getSportId());
                    ps.setInt(4,slip.getTeamId());
                    ps.setInt(5,slip.getMarketId());
                    ps.setString(6,slip.getMarketName());
                    ps.setString(7, slip.getParticipantName());
                    ps.setBigDecimal(8, BigDecimal.valueOf(slip.getOdds()));
                });
    }

    public List<Bet> fetchBets(Long profileId, String filter, int page, int size){
        String query = """
                SELECT
                    b.bet_id,
                    b.profile_id,
                    b.stake,
                    b.is_bonus,
                    b.status,
                    b.total_odds,
                    b.possible_win,
                    b.created_at,
                    s.bet_slip_id,
                    s.event_id,
                    s.sport_id,
                    s.team_id,
                    s.market_id,
                    s.market_name,
                    s.participant_name,
                    s.odds
                FROM (
                    SELECT
                        bet_id,
                        profile_id,
                        stake,
                        is_bonus,
                        status,
                        total_odds,
                        possible_win,
                        created_at
                    FROM bets
                    WHERE profile_id = ?
                """ + this.filterQuery(filter) + """
                    ORDER BY created_at DESC
                    LIMIT ? OFFSET ?
                ) b
                LEFT JOIN bet_slips s
                ON b.bet_id = s.bet_id
                ORDER BY b.created_at DESC
                """;

       return  this.jdbcTemplate.query(query,rs -> {
            Map<Long, Bet> betMap = new LinkedHashMap<>();

            while(rs.next()){
                Long betId = rs.getLong("bet_id");
                Integer betProfileId = rs.getInt("profile_id");
                BigDecimal stake = rs.getBigDecimal("stake");
                Integer isBonus = rs.getInt("is_bonus");
                Integer status = rs.getInt("status");
                BigDecimal totalOdds = rs.getBigDecimal("total_odds");
                BigDecimal possibleWin = rs.getBigDecimal("possible_win");
                LocalDateTime createdAt = rs.getObject("created_at", LocalDateTime.class);

                Bet bet = betMap.computeIfAbsent(betId, id -> {
                    Bet b = new Bet();
                    b.setBetId(id.intValue());
                    b.setProfileId(betProfileId);
                    b.setStake(stake);
                    b.setIsBonus(isBonus);
                    b.setStatus(status);
                    b.setTotalOdds(totalOdds);
                    b.setPossibleWin(possibleWin);
                    b.setCreated_at(createdAt);

                    b.setSlips(new ArrayList<>());

                    return b;

                });

                // Collect Slips related to bet
                Integer betSlipId = rs.getInt("bet_slip_id");
                Integer slipBetId = rs.getInt("bet_id");
                String eventId = rs.getString("event_id");
                Integer sportId = rs.getInt("sport_id");
                Integer teamId = rs.getInt("team_id");
                Integer marketId = rs.getInt("market_id");
                String marketName = rs.getString("market_name");
                String participantName = rs.getString("participant_name");
                BigDecimal odds = rs.getBigDecimal("odds");


                if(eventId != null){

                    Slip slip = new Slip();

                    slip.setBetSlipId(betSlipId);
                    slip.setEventId(eventId);
                    slip.setBetId(slipBetId);
                    slip.setSportId(sportId);
                    slip.setTeamId(teamId);
                    slip.setMarketId(marketId);
                    slip.setMarketName(marketName);
                    slip.setParticipantName(participantName);
                    slip.setOdds(odds);

                    bet.getSlips().add(slip);

                }

            }

            return new ArrayList<>(betMap.values());
        }, profileId, size, page * size);
    }

    public Bet fetchABet(Long profileId, Long betId){
        //STEP 01:: Fetch the bet
        String queryOne = """
                SELECT
                    bet_id,
                    profile_id,
                    stake,
                    is_bonus,
                    status,
                    total_odds,
                    possible_win,
                    created_at
                FROM bets
                WHERE bet_id = ? AND profile_id =?
                """;

        Bet bet;
        try{
            bet = this.jdbcTemplate.queryForObject(queryOne,this.betRowMapper,betId,profileId);
            // queryForObject() -> When you want exactly one row
        }catch(EmptyResultDataAccessException e){
            return null;
        }


        // STEP 02:: Fetch the slips
        String queryTwo = """
                SELECT
                    bet_slip_id,
                    bet_id,
                    event_id,
                    sport_id,
                    team_id,
                    market_id,
                    market_name,
                    participant_name,
                    odds
                FROM bet_slips
                WHERE bet_id = ?
                """;


        if(bet != null){
            List<Slip> slips = this.jdbcTemplate.query(queryTwo,this.slipRowMapper,betId);
            // query() -> when you expect multiple rows
            bet.setSlips(slips);
        }

        return bet;
    }

    private String filterQuery(String filter){
        switch(filter.toLowerCase()){
            case "day" -> {
                return "AND created_at >= CURRENT_DATE AND created_at < DATE_ADD(CURRENT_DATE, INTERVAL 1 DAY)";
            }

            case "week" -> {
                return "AND created_at >= STR_TO_DATE(CONCAT(YEARWEEK(CURRENT_DATE, 1), ' Monday'), '%x%v %W') " +
                        "AND created_at < DATE_ADD(STR_TO_DATE(CONCAT(YEARWEEK(CURRENT_DATE, 1), ' Monday'), '%x%v %W'), INTERVAL 7 DAY) ";
            }

            case "month" -> {
                return "AND created_at >= (LAST_DAY(CURRENT_DATE - INTERVAL 1 MONTH) + INTERVAL 1 DAY) " +
                        "AND created_at < (LAST_DAY(CURRENT_DATE) + INTERVAL 1 DAY) ";
            }

            case "all" -> {
                return ""; // no filter
            }

            default -> throw new IllegalArgumentException("Invalid filter: " + filter);
        }

    }
}
