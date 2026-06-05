package com.bicosteve.api_gateway.repository;

import com.bicosteve.api_gateway.models.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TransactionRepository {
    private final JdbcTemplate jdbcTemplate;

    public int addTransaction(Transaction transaction) {

        String query = """
                INSERT INTO transactions
                    (profile_id, reference, type, amount, status, created_by)
                VALUES(?, ?, ?, ?, ?, ?)
                """;

        return this.jdbcTemplate.update(
                query,
                transaction.getProfileId(),
                transaction.getReference(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getStatus(),
                transaction.getCreatedBy()
                );

    }
}
