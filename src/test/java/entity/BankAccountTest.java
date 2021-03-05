package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class BankAccountTest {
    private BankAccount bankAccount;

    @BeforeEach
    void beforeEach() {
        this.bankAccount = this.getBankAccount();
    }

    private BankAccount getBankAccount() {
        return Mockito.mock(BankAccount.class, Mockito.withSettings()
                .useConstructor(1L, new Owner(1L, "Marcelo", "Chiaradia")).defaultAnswer(Mockito.CALLS_REAL_METHODS));
    }

    @Test
    void newBankAccountBalance() {
        assertEquals(this.bankAccount.getBalance(), BigDecimal.ZERO);
    }

    @Test
    void depositPositiveAmount() {
        final BigDecimal depositAmount = BigDecimal.valueOf(100);
        this.bankAccount.deposit(depositAmount);
        assertEquals(this.bankAccount.getBalance(), depositAmount);
    }

    @Test
    void depositNegativeAmount() {
        final BigDecimal depositAmount = BigDecimal.valueOf(-100);
        assertThrows(IllegalArgumentException.class, () -> this.bankAccount.deposit(depositAmount));
        assertEquals(this.bankAccount.getBalance(), BigDecimal.ZERO);
    }

    @Test
    void depositCeroAmount() {
        final BigDecimal depositAmount = BigDecimal.ZERO;
        this.bankAccount.deposit(depositAmount);
        assertEquals(this.bankAccount.getBalance(), BigDecimal.ZERO);
    }

    @Test
    void withdrawWithEnoughFunds() throws InsufficientFundsException {
        final BigDecimal amount = BigDecimal.valueOf(100);
        this.bankAccount.deposit(amount);
        assertEquals(this.bankAccount.getBalance(), amount);
        given(this.bankAccount.mayWithdraw(eq(amount))).willReturn(true);
        this.bankAccount.withdraw(amount);
        assertEquals(this.bankAccount.getBalance(), BigDecimal.ZERO);
    }

    @Test
    void withdrawalWithInsufficientFunds() {
        final BigDecimal amount = BigDecimal.valueOf(100);
        given(this.bankAccount.mayWithdraw(eq(amount))).willReturn(false);
        assertThrows(InsufficientFundsException.class, () -> this.bankAccount.withdraw(amount));
        assertEquals(this.bankAccount.getBalance(), BigDecimal.ZERO);
    }
}
