package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class BankAccountTest {
    @Test
    void depositSuccess() {
        final BankAccount bankAccount = Mockito.mock(BankAccount.class, Mockito.withSettings()
                .useConstructor(1L, new Owner(1L, "Marcelo", "Chiaradia")).defaultAnswer(Mockito.CALLS_REAL_METHODS));
        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);

        final BigDecimal depositAmount = BigDecimal.valueOf(100);
        bankAccount.deposit(depositAmount);

        assertEquals(bankAccount.getBalance(), depositAmount);
    }

    @Test
    void withdrawalWithEnoughFunds() throws InsufficientFundsException {
        final BankAccount bankAccount = Mockito.mock(BankAccount.class, Mockito.withSettings()
                .useConstructor(1L, new Owner(1L, "Marcelo", "Chiaradia")).defaultAnswer(Mockito.CALLS_REAL_METHODS));
        final BigDecimal amount = BigDecimal.valueOf(100);
        bankAccount.deposit(amount);
        assertEquals(bankAccount.getBalance(), amount);

        given(bankAccount.mayWithdraw(eq(amount))).willReturn(true);

        bankAccount.withdraw(amount);
        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);
    }

    @Test
    void withdrawalWithInsufficientFunds() {
        final BankAccount bankAccount = Mockito.mock(BankAccount.class, Mockito.withSettings()
                .useConstructor(1L, new Owner(1L, "Marcelo", "Chiaradia")).defaultAnswer(Mockito.CALLS_REAL_METHODS));
        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);

        final BigDecimal amount = BigDecimal.valueOf(100);
        given(bankAccount.mayWithdraw(eq(amount))).willReturn(false);

        assertThrows(InsufficientFundsException.class, () -> bankAccount.withdraw(BigDecimal.valueOf(100)));
        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);
    }

}
