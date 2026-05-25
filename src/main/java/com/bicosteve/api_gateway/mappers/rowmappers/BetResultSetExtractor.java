package com.bicosteve.api_gateway.mappers.rowmappers;

import com.bicosteve.api_gateway.models.Bet;
import com.bicosteve.api_gateway.models.Slip;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class BetResultSetExtractor  implements ResultSetExtractor<List<Bet>> {
    @Override
    public List<Bet> extractData(ResultSet rs) throws SQLException, DataAccessException{
        Map<Long, Bet> betMap = new LinkedHashMap<>();

        while (rs.next()) {
            Long betId                      = rs.getLong("bet_id");
            Long profileId                  = rs.getLong("profile_id");
            BigDecimal stake                = rs.getBigDecimal("stake");
            Integer isBonus                 = rs.getInt("is_bonus");
            Integer betStatus               = rs.getInt("bet_status");
            BigDecimal totalOdds            = rs.getBigDecimal("total_odds");
            BigDecimal possibleWin          = rs.getBigDecimal("possible_win");
            LocalDateTime createdAt         = rs.getObject("bet_created_at", LocalDateTime.class);
            LocalDateTime updatedAt         = rs.getObject("bet_updated_at", LocalDateTime.class);

            Bet bet = betMap.computeIfAbsent(betId, id -> {
                Bet b = new Bet();
                b.setBetId(id);
                b.setProfileId(profileId);
                b.setStake(stake);
                b.setIsBonus(isBonus);
                b.setStatus(betStatus);
                b.setTotalOdds(totalOdds);
                b.setPossibleWin(possibleWin);
                b.setCreatedAt(createdAt);
                b.setUpdatedAt(updatedAt);
                b.setSlips(new ArrayList<>());

                return b;
            });

            String eventId = rs.getString("event_id");
            if(eventId != null) {
                Slip slip = this.extractSlip(rs, eventId);
                bet.getSlips().add(slip);
            }
        }

        return new ArrayList<>(betMap.values());
    }


    private Slip extractSlip(ResultSet rs, String eventId) throws SQLException, DataAccessException {
        Slip slip = new Slip();
        slip.setBetSlipId(rs.getInt("bet_slip_id"));
        slip.setBetId(rs.getInt("slip_bet_id"));
        slip.setEventId(eventId);
        slip.setSportId(rs.getInt("sport_id"));
        slip.setTeamId(rs.getInt("team_id"));
        slip.setMarketId(rs.getInt("market_id"));
        slip.setMarketName(rs.getString("market_name"));
        slip.setParticipantName(rs.getString("participant_name"));
        slip.setOdds(rs.getBigDecimal("odds"));
        slip.setSpecialBetValue(rs.getString("special_bet_value"));
        slip.setStatus(rs.getInt("slip_status"));
        slip.setCreatedAt(rs.getObject("slip_created_at", LocalDateTime.class));
        slip.setUpdatedAt(rs.getObject("slip_updated_at", LocalDateTime.class));

        return slip;
    }
}
