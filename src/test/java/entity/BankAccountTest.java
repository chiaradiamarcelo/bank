package entity;

import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class BankAccountTest {
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
        assertEquals(ZERO, this.bankAccount.getBalance());
    }

    @Test
    void depositPositiveAmount() {
        final BigDecimal depositAmount = BigDecimal.valueOf(100);
        this.bankAccount.deposit(depositAmount);
        assertEquals(depositAmount, this.bankAccount.getBalance());
    }

    @Test
    void depositNegativeAmount() {
        final BigDecimal depositAmount = BigDecimal.valueOf(-100);
        assertThrows(IllegalArgumentException.class, () -> this.bankAccount.deposit(depositAmount));
        assertEquals(ZERO, this.bankAccount.getBalance());
    }

    @Test
    void depositCeroAmount() {
        final BigDecimal depositAmount = ZERO;
        this.bankAccount.deposit(depositAmount);
        assertEquals(ZERO, this.bankAccount.getBalance());
    }

    @Test
    void withdrawWithEnoughFunds() throws InsufficientFundsException {
        final BigDecimal amount = BigDecimal.valueOf(100);
        given(this.bankAccount.mayWithdraw(amount)).willReturn(true);
        this.bankAccount.withdraw(amount);
        assertEquals(amount.negate(), this.bankAccount.getBalance());
    }

    @Test
    void withdrawalWithInsufficientFunds() {
        final BigDecimal amount = BigDecimal.valueOf(100);
        given(this.bankAccount.mayWithdraw(amount)).willReturn(false);
        assertThrows(InsufficientFundsException.class, () -> this.bankAccount.withdraw(amount));
        assertEquals(ZERO, this.bankAccount.getBalance());
    }
}
