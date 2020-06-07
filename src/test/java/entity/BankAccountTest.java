package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doCallRealMethod;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

public class BankAccountTest {

    @Test
    void depositSuccess() {
        BankAccount bankAccount = Mockito.mock(BankAccount.class, Mockito.withSettings()
                .useConstructor(1L, new Owner(1L, "Marcelo", "Chiaradia")).defaultAnswer(Mockito.CALLS_REAL_METHODS));
        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);

        BigDecimal depositAmount = BigDecimal.valueOf(100);
        bankAccount.deposit(depositAmount);

        assertEquals(bankAccount.getBalance(), depositAmount);
    }

    @Test
    void withdrawalSuccess_With_Enough_Funds() throws InsufficientFundsException {
        BankAccount bankAccount = Mockito.mock(BankAccount.class, Mockito.withSettings()
                .useConstructor(1L, new Owner(1L, "Marcelo", "Chiaradia")).defaultAnswer(Mockito.CALLS_REAL_METHODS));
        BigDecimal amount = BigDecimal.valueOf(100);
        bankAccount.deposit(amount);
        assertEquals(bankAccount.getBalance(), amount);

        given(bankAccount.mayWithdraw(eq(amount))).willReturn(true);

        bankAccount.withdraw(amount);
        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);
    }

    @Test
    void withdrawalFailed_With_Insufficient_Funds() {
        BankAccount bankAccount = Mockito.mock(BankAccount.class, Mockito.withSettings()
                .useConstructor(1L, new Owner(1L, "Marcelo", "Chiaradia")).defaultAnswer(Mockito.CALLS_REAL_METHODS));
        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);

        BigDecimal amount = BigDecimal.valueOf(100);
        given(bankAccount.mayWithdraw(eq(amount))).willReturn(false);

        assertThrows(InsufficientFundsException.class, () -> bankAccount.withdraw(BigDecimal.valueOf(100)));
        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);
    }

}
