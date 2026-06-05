package com.bicosteve.api_gateway.repository;

import com.bicosteve.api_gateway.mappers.rowmappers.WalletRowMapper;
import com.bicosteve.api_gateway.models.Wallet;
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
    private final WalletRowMapper walletRowMapper;

    /**
     * Credits a user's wallet balance. Use this when usr makes a deposit.
     * @param profileId the profile that owns the wallet
     * @param amount amount to add to the balance
     * @return true when exactly one wallets row was updated
     */
    public boolean creditWalletBalance(Long profileId, BigDecimal amount) {
        this.validateAmount(amount);

        String query = """
                UPDATE
                    wallets
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
     * @param profileId the profile that owns the wallet
     * @param amount amount to subtract from the balance
     * @return true when exactly one wallets row was updated
     */
    public boolean debitWalletBalance(Long profileId, BigDecimal amount) {
        // STEP 01: Check the incoming amount
        this.validateAmount(amount);

        // STEP 02: Check if the account exists
        // or if the balance is greater than the amount requested for withdrawing
        Wallet wallet = this.checkAccountBalance(profileId);
        if(wallet == null || wallet.getBalance().compareTo(amount) < 0) {
            return false;
        }


        String query = """
                UPDATE
                    wallets
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

    /**
     * Fetches the user wallet
     * @param profileId;
     * @return Wallet
     * **/
    private Wallet checkAccountBalance(Long profileId){
        String query = "SELECT * FROM wallets WHERE profile_id = ?";
        return this.jdbcTemplate.queryForObject(query,this.walletRowMapper, profileId);
    }
}