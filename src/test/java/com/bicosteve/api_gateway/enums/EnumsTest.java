package com.bicosteve.api_gateway.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnumsTest {

    @Test
    void depositStatusHasExpectedValues() {
        assertEquals(0, DepositStatus.PENDING.getStatus());
        assertEquals(1, DepositStatus.SUCCESS.getStatus());
        assertEquals(2, DepositStatus.FAILED.getStatus());
    }

    @Test
    void transactionStatusHasExpectedValues() {
        assertEquals(0, TransactionStatus.PENDING.getStatus());
        assertEquals(1, TransactionStatus.SUCCESS.getStatus());
        assertEquals(2, TransactionStatus.FAILED.getStatus());
    }

    @Test
    void transactionTypeHasExpectedValues() {
        assertEquals(1, TransactionType.CREDIT.getStatus());
        assertEquals(2, TransactionType.DEBIT.getStatus());
    }

    @Test
    void depositStatusValueOf() {
        assertSame(DepositStatus.PENDING, DepositStatus.valueOf("PENDING"));
        assertSame(DepositStatus.SUCCESS, DepositStatus.valueOf("SUCCESS"));
    }

    @Test
    void transactionStatusValueOf() {
        assertSame(TransactionStatus.PENDING, TransactionStatus.valueOf("PENDING"));
    }

    @Test
    void transactionTypeValueOf() {
        assertSame(TransactionType.CREDIT, TransactionType.valueOf("CREDIT"));
        assertSame(TransactionType.DEBIT, TransactionType.valueOf("DEBIT"));
    }
}
