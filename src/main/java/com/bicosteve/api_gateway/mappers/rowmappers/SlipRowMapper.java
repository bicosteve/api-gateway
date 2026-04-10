package com.bicosteve.api_gateway.mappers.rowmappers;

import com.bicosteve.api_gateway.models.Slip;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class SlipRowMapper implements RowMapper<Slip>{

    @Override
    public Slip mapRow(ResultSet rs, int rowNum) throws SQLException{

        Slip slip = new Slip();

        slip.setBetSlipId(rs.getInt("bet_slip_id"));
        slip.setBetId(rs.getInt("bet_id"));
        slip.setEventId(rs.getString("event_id"));
        slip.setSportId(rs.getInt("sport_id"));
        slip.setTeamId(rs.getInt("team_id"));
        slip.setMarketId(rs.getInt("market_id"));
        slip.setMarketName(rs.getString("market_name"));
        slip.setParticipantName(rs.getString("participant_name"));
        slip.setOdds(rs.getBigDecimal("odds"));

        return slip;
    }
}
