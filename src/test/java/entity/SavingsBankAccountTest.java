package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class SavingsBankAccountTest {

    @Test
    void calculateInterestSuccess_With_No_Interest_Rate_And_No_Balance() {
        SavingsBankAccount bankAccount = new SavingsBankAccount(1L, new Owner(1L, "Marcelo", "Chiaradia"), 0L);
        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);

        assertEquals(bankAccount.calculateInterest(), BigDecimal.ZERO);
    }

    @Test
    void calculateInterestSuccess_With_No_Balance_And_Positive_Interest_Rate() {
        long interestRate = 10L;
        SavingsBankAccount bankAccount =
                new SavingsBankAccount(1L, new Owner(1L, "Marcelo", "Chiaradia"), interestRate);
        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);

        assertEquals(bankAccount.calculateInterest(), BigDecimal.ZERO);
    }

    @Test
    void calculateInterestSuccess_With_Positive_Balance_And_Positive_Interest_Rate() {
        long interestRate = 10L;
        SavingsBankAccount bankAccount =
                new SavingsBankAccount(1L, new Owner(1L, "Marcelo", "Chiaradia"), interestRate);
        BigDecimal depositAmount = BigDecimal.valueOf(100);
        bankAccount.deposit(depositAmount);
        assertEquals(bankAccount.getBalance(), depositAmount);

        BigDecimal interest =
                BigDecimal.valueOf(interestRate).multiply(bankAccount.getBalance()).divide(BigDecimal.valueOf(100));
        assertEquals(bankAccount.calculateInterest(), interest);
    }

    @Test
    void mayWithdrawalFailed_With_Insufficient_Funds() {
        SavingsBankAccount bankAccount = new SavingsBankAccount(1L, new Owner(1L, "Marcelo", "Chiaradia"), 0L);
        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);

        assertFalse(bankAccount.mayWithdraw(BigDecimal.valueOf(100)));
    }

    @Test
    void mayWithdrawalSuccess_With_Sufficient_Funds() {
        SavingsBankAccount bankAccount = new SavingsBankAccount(1L, new Owner(1L, "Marcelo", "Chiaradia"), 0L);
        BigDecimal amount = BigDecimal.valueOf(100);
        bankAccount.deposit(amount);
        assertEquals(bankAccount.getBalance(), amount);

        assertTrue(bankAccount.mayWithdraw(amount));
    }

}
