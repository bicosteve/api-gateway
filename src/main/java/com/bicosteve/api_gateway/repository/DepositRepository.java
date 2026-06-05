package com.bicosteve.api_gateway.repository;

import com.bicosteve.api_gateway.mappers.rowmappers.DepositRowMapper;
import com.bicosteve.api_gateway.models.Deposit;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DepositRepository {
    private final JdbcTemplate jdbcTemplate;
    private final DepositRowMapper depositRowMapper;

    public Deposit findByTrxRef(String trxRef) {
        String query = "SELECT * FROM deposits WHERE transaction_ref = ?";
        return this.jdbcTemplate.queryForObject(query, this.depositRowMapper, trxRef);
    }

    public void updateDepositStatus(String trxRef, Integer status) {
        String query = "UPDTE deposits SET status = ? WHERE transaction_ref = ?";
        this.jdbcTemplate.update(query, status, trxRef);
    }

    public void insertDeposit(Deposit deposit) {
        String sql = "INSERT INTO deposit (transaction_ref, amount, status) VALUES (?, ?, ?)";
        this.jdbcTemplate.update(sql, deposit.getTrxRef(), deposit.getAmount(), deposit.getStatus());
    }
}
