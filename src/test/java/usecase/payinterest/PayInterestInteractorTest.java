package usecase.payinterest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import entity.Owner;
import entity.SavingsBankAccount;
import gateway.AccountLocker;
import gateway.BankAccountRepository;
import gateway.TransactionManager;
import usecase.exception.BankAccountNotFoundException;

public class PayInterestInteractorTest {

    @SuppressWarnings("unchecked")
    private BankAccountRepository<SavingsBankAccount> bankAccountRepository = Mockito.mock(BankAccountRepository.class);
    private AccountLocker accountLocker = Mockito.mock(AccountLocker.class);
    private TransactionManager transactionManager = Mockito.mock(TransactionManager.class);

    private PayInterestInteractor payInterestService =
            new PayInterestInteractor(this.accountLocker, this.bankAccountRepository, this.transactionManager);

    @Test
    void payInterestSuccess_With_Positive_Balance_And_Positive_Interest_Rate_In_Bank_Account()
        throws BankAccountNotFoundException {
        long accountID = 1L;
        long interestRate = 10L;
        BigDecimal amount = BigDecimal.valueOf(100);

        SavingsBankAccount bankAccount =
                new SavingsBankAccount(accountID, new Owner(1L, "Marcelo", "Chiaradia"), interestRate);
        bankAccount.deposit(amount);
        assertEquals(bankAccount.getBalance(), amount);

        given(this.bankAccountRepository.getByAccountID(eq(accountID))).willReturn(Optional.of(bankAccount));

        this.payInterestService.payInterest(accountID);

        then(this.transactionManager).should().beginTransaction();
        then(this.accountLocker).should().lockByAccountID(eq(accountID));
        then(this.bankAccountRepository).should().save(eq(bankAccount));
        then(this.accountLocker).should().unlockByAccountID(eq(accountID));
        then(this.transactionManager).should().commitTransaction();

        // interestRate * balance / 100
        BigDecimal interest = BigDecimal.valueOf(interestRate).multiply(amount).divide(BigDecimal.valueOf(100));
        assertEquals(bankAccount.getBalance(), amount.add(interest));
    }

    @Test
    void payInterestSuccess_With_No_Balance_In_Bank_Account() throws BankAccountNotFoundException {
        long accountID = 1L;
        long interestRate = 10L;

        SavingsBankAccount bankAccount =
                new SavingsBankAccount(accountID, new Owner(1L, "Marcelo", "Chiaradia"), interestRate);
        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);

        given(this.bankAccountRepository.getByAccountID(eq(accountID))).willReturn(Optional.of(bankAccount));

        this.payInterestService.payInterest(accountID);


        then(this.accountLocker).should().lockByAccountID(eq(accountID));
        then(this.accountLocker).should().unlockByAccountID(eq(accountID));
        then(this.bankAccountRepository).should().save(eq(bankAccount));
        then(this.transactionManager).should().beginTransaction();
        then(this.transactionManager).should().commitTransaction();

        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);
    }

    @Test
    void payInterestFailed_Bank_Account_Not_Found() {
        long accountID = 1L;

        given(this.bankAccountRepository.getByAccountID(eq(accountID))).willReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class, () -> this.payInterestService.payInterest(accountID));

        then(this.transactionManager).should().beginTransaction();
        then(this.transactionManager).should().rollbackTransaction();
    }

}
