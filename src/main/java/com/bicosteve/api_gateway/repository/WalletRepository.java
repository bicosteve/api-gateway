package com.bicosteve.api_gateway.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
@RequiredArgsConstructor
@Slf4j
public class WalletRepository {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Credits a user's wallet balance. Use this when usr makes a deposit.
     *
     * @param profileId the profile that owns the wallet
     * @param amount amount to add to the balance
     * @return true when exactly one wallet row was updated
     */
    public boolean creditWalletBalance(Long profileId, BigDecimal amount) {
        this.validateAmount(amount);

        String query = """
                UPDATE
                    wallet
                SET
                    balance = balance + ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE
                    profile_id = ?
                """;

        return this.updateWalletBalance(query, amount, profileId);
    }

    /**
     * Debits a user's wallet balance. The WHERE clause prevents the wallet from
     * becoming negative when placing a bet or processing a withdrawal.
     *
     * @param profileId the profile that owns the wallet
     * @param amount amount to subtract from the balance
     * @return true when exactly one wallet row was updated
     */
    public boolean debitWalletBalance(Long profileId, BigDecimal amount) {
        this.validateAmount(amount);

        String query = """
                UPDATE
                    wallet
                SET
                    balance = balance - ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE
                    profile_id = ?
                    AND balance >= ?
                """;

        return this.updateWalletBalance(query, amount, profileId, amount);
    }

    private boolean updateWalletBalance(String query, Object... args) {
        try {
            int affectedRows = this.jdbcTemplate.update(query, args);
            return affectedRows == 1;
        } catch (DataAccessException e) {
            log.warn("Error updating wallet balance", e);
            throw e;
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Wallet update amount must be greater than zero");
        }
    }

//    private void checkAccountBalance(Long profileId){
//        String query = "SELECT balance FROM wallet WHERE profile_id = ?";
//        this.jdbcTemplate.query(query);
//    }
}