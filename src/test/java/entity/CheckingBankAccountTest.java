package entity;

import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class CheckingBankAccountTest {
    @Test
    void mayWithdrawalWithInsufficientFunds() {
        final CheckingBankAccount bankAccount = this.getCheckingBankAccount();
        assertEquals(ZERO, bankAccount.getBalance());
        assertFalse(bankAccount.mayWithdraw(BigDecimal.valueOf(100)));
    }

    @Test
    void mayWithdrawalWithSufficientFunds() {
        final CheckingBankAccount bankAccount = this.getCheckingBankAccount();
        final BigDecimal amount = BigDecimal.valueOf(100);
        bankAccount.deposit(amount);
        assertEquals(bankAccount.getBalance(), amount);
        assertTrue(bankAccount.mayWithdraw(amount));
    }

    private CheckingBankAccount getCheckingBankAccount() {
        return this.getCheckingBankAccountWithOverdraftLimit(ZERO);
    }

    private CheckingBankAccount getCheckingBankAccountWithOverdraftLimit(final BigDecimal overdraftLimit) {
        return new CheckingBankAccount(1L, new Owner(1L, "Marcelo", "Chiaradia"), overdraftLimit);
    }

    @Test
    void mayWithdrawalWithNoBalanceAndBalanceOverdraftAllowed() throws InsufficientFundsException {
        final BigDecimal overdraftLimit = BigDecimal.valueOf(100);
        final CheckingBankAccount bankAccount = this.getCheckingBankAccountWithOverdraftLimit(overdraftLimit);
        assertEquals(ZERO, bankAccount.getBalance());
        assertTrue(bankAccount.mayWithdraw(overdraftLimit));
    }
}
