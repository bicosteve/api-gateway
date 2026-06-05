package com.bicosteve.api_gateway.mappers.rowmappers;

import com.bicosteve.api_gateway.models.Deposit;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class DepositRowMapper implements RowMapper<Deposit> {
    @Override
    public Deposit mapRow(ResultSet rs, int rowNum) throws SQLException{
        Deposit deposit = new Deposit();

        deposit.setTrxRef(rs.getString("trxRef"));
        deposit.setAmount(rs.getBigDecimal("amount"));
        deposit.setProfileId(rs.getLong("profile_id"));
        deposit.setStatus(rs.getInt("status"));
        deposit.setCurrency(rs.getString("currency"));
        deposit.setCheckoutUrl(rs.getString("checkout_url"));

        return deposit;
    }
}
