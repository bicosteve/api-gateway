package com.bicosteve.api_gateway.mappers.rowmappers;

import com.bicosteve.api_gateway.models.Wallet;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Component
public class WalletRowMapper implements RowMapper<Wallet> {

    @Override
    public Wallet mapRow(ResultSet rs, int rowNum) throws SQLException{
        Wallet wallet = new Wallet();

        wallet.setId(rs.getInt("id"));
        wallet.setProfileId(rs.getInt("profile_id"));
        wallet.setBalance(rs.getBigDecimal("balance"));
        wallet.setBonus(rs.getBigDecimal("bonus"));
        wallet.setCreatedBy(rs.getString("created_by"));
        wallet.setCreatedAt(rs.getObject("created_at",LocalDateTime.class));
        wallet.setUpdatedAt(rs.getObject("updated_at",LocalDateTime.class));

        return wallet;
    }
}
