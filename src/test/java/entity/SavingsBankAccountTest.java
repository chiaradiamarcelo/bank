package entity;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class SavingsBankAccountTest {
    @Test
    void calculateInterestWithNoInterestRateAndNoBalance() {
        final SavingsBankAccount bankAccount = new SavingsBankAccount(1L, new Owner(1L, "Marcelo", "Chiaradia"), 0L);
        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);
        assertEquals(bankAccount.calculateInterest(), BigDecimal.ZERO);
    }

    @Test
    void calculateInterestWithNoBalanceAndPositiveInterest_Rate() {
        final long interestRate = 10L;
        final SavingsBankAccount bankAccount = new SavingsBankAccount(1L, new Owner(1L, "Marcelo", "Chiaradia"),
                interestRate);
        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);
        assertEquals(bankAccount.calculateInterest(), BigDecimal.ZERO);
    }

    @Test
    void calculateInterestWithPositiveBalanceAndPositiveInterestRate() {
        final long interestRate = 10L;
        final SavingsBankAccount bankAccount = new SavingsBankAccount(1L, new Owner(1L, "Marcelo", "Chiaradia"),
                interestRate);
        final BigDecimal depositAmount = BigDecimal.valueOf(100);
        bankAccount.deposit(depositAmount);
        assertEquals(bankAccount.getBalance(), depositAmount);

        final BigDecimal interest = BigDecimal.valueOf(interestRate).multiply(bankAccount.getBalance())
                .divide(BigDecimal.valueOf(100));
        assertEquals(bankAccount.calculateInterest(), interest);
    }

    @Test
    void mayWithdrawalWithInsufficientFunds() {
        final SavingsBankAccount bankAccount = new SavingsBankAccount(1L, new Owner(1L, "Marcelo", "Chiaradia"), 0L);
        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);
        assertFalse(bankAccount.mayWithdraw(BigDecimal.valueOf(100)));
    }

    @Test
    void mayWithdrawalWithSufficientFunds() {
        final SavingsBankAccount bankAccount = new SavingsBankAccount(1L, new Owner(1L, "Marcelo", "Chiaradia"), 0L);
        final BigDecimal amount = BigDecimal.valueOf(100);
        bankAccount.deposit(amount);
        assertEquals(bankAccount.getBalance(), amount);
        assertTrue(bankAccount.mayWithdraw(amount));
    }

}
