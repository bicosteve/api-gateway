package com.bicosteve.api_gateway.mappers.rowmappers;

import com.bicosteve.api_gateway.models.Bet;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Component
public class BetRowMapper implements RowMapper<Bet>{

    @Override
    public Bet mapRow(ResultSet rs, int rowNum) throws SQLException{

        Bet bet = new Bet();

        bet.setBetId(rs.getInt("bet_id"));
        bet.setProfileId(rs.getLong("profile_id"));
        bet.setStake(rs.getBigDecimal("stake"));
        bet.setIsBonus(rs.getInt("is_bonus"));
        bet.setStatus(rs.getInt("bet_status"));
        bet.setTotalOdds(rs.getBigDecimal("total_odds"));
        bet.setPossibleWin(rs.getBigDecimal("possible_win"));
        bet.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));

        return bet;
    }

}
