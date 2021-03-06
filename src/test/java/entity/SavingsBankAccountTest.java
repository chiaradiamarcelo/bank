package entity;

import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class SavingsBankAccountTest {
    @Test
    void calculateInterestWithNoInterestRateAndNoBalance() {
        final SavingsBankAccount bankAccount = this.getSavingsBankAccount();
        assertEquals(ZERO, bankAccount.getBalance());
        assertEquals(ZERO, bankAccount.calculateInterest());
    }

    private SavingsBankAccount getSavingsBankAccount() {
        return this.getSavingsBankAccountWithInterestRate(0L);
    }

    private SavingsBankAccount getSavingsBankAccountWithInterestRate(final long interestRate) {
        return new SavingsBankAccount(1L, new Owner(1L, "Marcelo", "Chiaradia"), interestRate);
    }

    @Test
    void calculateInterestWithNoBalanceAndPositiveInterestRate() {
        final SavingsBankAccount bankAccount = this.getSavingsBankAccountWithInterestRate(10L);
        assertEquals(ZERO, bankAccount.getBalance());
        assertEquals(ZERO, bankAccount.calculateInterest());
    }

    @Test
    void calculateInterestWithPositiveBalanceAndPositiveInterestRate() {
        final long interestRate = 10L;
        final SavingsBankAccount bankAccount = this.getSavingsBankAccountWithInterestRate(10L);
        final BigDecimal depositAmount = BigDecimal.valueOf(100);
        bankAccount.deposit(depositAmount);
        final BigDecimal interest = calculateInterestFor(interestRate, bankAccount, depositAmount);
        assertEquals(bankAccount.calculateInterest(), interest);
    }

    private BigDecimal calculateInterestFor(final long interestRate, final SavingsBankAccount bankAccount,
            final BigDecimal depositAmount) {
        return BigDecimal.valueOf(interestRate).multiply(bankAccount.getBalance()).divide(depositAmount);
    }

    @Test
    void mayWithdrawalWithInsufficientFunds() {
        final SavingsBankAccount bankAccount = this.getSavingsBankAccount();
        assertEquals(ZERO, bankAccount.getBalance());
        assertFalse(bankAccount.mayWithdraw(BigDecimal.valueOf(100)));
    }

    @Test
    void mayWithdrawalWithSufficientFunds() {
        final SavingsBankAccount bankAccount = this.getSavingsBankAccount();
        final BigDecimal amount = BigDecimal.valueOf(100);
        bankAccount.deposit(amount);
        assertEquals(bankAccount.getBalance(), amount);
        assertTrue(bankAccount.mayWithdraw(amount));
    }
}
