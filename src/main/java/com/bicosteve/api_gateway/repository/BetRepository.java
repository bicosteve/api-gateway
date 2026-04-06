package com.bicosteve.api_gateway.repository;

import com.bicosteve.api_gateway.dto.requests.BetRequest;
import com.bicosteve.api_gateway.dto.requests.SlipRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BetRepository{
    private final JdbcTemplate jdbcTemplate;


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

        int rowsAffected = this.jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"bet_id"});

            ps.setString(1, request.getProfileId());
            ps.setDouble(2,request.getStake());
            ps.setInt(3,request.getIsBonus());
            ps.setDouble(4,request.getStake());
            ps.setDouble(5,request.getTotalOdds().doubleValue());
            ps.setDouble(6,possibleWin);

            return ps;

        }, keyHolder);

        Long betId = null;

        if(rowsAffected != 0 && keyHolder.getKey() != null){
            betId = keyHolder.getKey().longValue();
        }

        return betId;
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
}
