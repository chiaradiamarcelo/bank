package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class CheckingBankAccountTest {

    @Test
    void mayWithdrawalFailed_With_Insufficient_Funds() {
        CheckingBankAccount bankAccount =
                new CheckingBankAccount(1L, new Owner(1L, "Marcelo", "Chiaradia"), BigDecimal.ZERO);
        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);

        assertFalse(bankAccount.mayWithdraw(BigDecimal.valueOf(100)));
    }

    @Test
    void mayWithdrawalSuccess_With_Sufficient_Funds() {
        CheckingBankAccount bankAccount =
                new CheckingBankAccount(1L, new Owner(1L, "Marcelo", "Chiaradia"), BigDecimal.ZERO);
        BigDecimal amount = BigDecimal.valueOf(100);
        bankAccount.deposit(amount);
        assertEquals(bankAccount.getBalance(), amount);

        assertTrue(bankAccount.mayWithdraw(amount));
    }

    @Test
    void mayWithdrawalSuccess_With_No_Balance_And_Balance_Overdraft_Allowed() throws InsufficientFundsException {
        BigDecimal overdraftLimit = BigDecimal.valueOf(100);
        CheckingBankAccount bankAccount =
                new CheckingBankAccount(1L, new Owner(1L, "Marcelo", "Chiaradia"), overdraftLimit);
        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);

        assertTrue(bankAccount.mayWithdraw(overdraftLimit));
    }

}
