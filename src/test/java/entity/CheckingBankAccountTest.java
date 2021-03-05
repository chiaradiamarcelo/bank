package entity;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class CheckingBankAccountTest {
    @Test
    void mayWithdrawalWithInsufficientFunds() {
        final CheckingBankAccount bankAccount = this.getBankAccount();
        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);
        assertFalse(bankAccount.mayWithdraw(BigDecimal.valueOf(100)));
    }

    @Test
    void mayWithdrawalWithSufficientFunds() {
        final CheckingBankAccount bankAccount = this.getBankAccount();
        final BigDecimal amount = BigDecimal.valueOf(100);
        bankAccount.deposit(amount);
        assertEquals(bankAccount.getBalance(), amount);
        assertTrue(bankAccount.mayWithdraw(amount));
    }

    private CheckingBankAccount getBankAccount() {
        return this.getBankAccountWithOverdraftLimit(BigDecimal.ZERO);
    }

    private CheckingBankAccount getBankAccountWithOverdraftLimit(final BigDecimal overdraftLimit) {
        return new CheckingBankAccount(1L, new Owner(1L, "Marcelo", "Chiaradia"), overdraftLimit);
    }

    @Test
    void mayWithdrawalWithNoBalanceAndBalanceOverdraftAllowed() throws InsufficientFundsException {
        final BigDecimal overdraftLimit = BigDecimal.valueOf(100);
        final CheckingBankAccount bankAccount = this.getBankAccountWithOverdraftLimit(overdraftLimit);
        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);
        assertTrue(bankAccount.mayWithdraw(overdraftLimit));
    }
}
