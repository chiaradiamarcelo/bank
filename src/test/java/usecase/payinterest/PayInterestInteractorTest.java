package usecase.payinterest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import entity.Owner;
import entity.SavingsBankAccount;
import gateway.*;
import usecase.exception.BankAccountNotFoundException;

public class PayInterestInteractorTest {
    @SuppressWarnings("unchecked")
    private final BankAccountRepository<SavingsBankAccount> bankAccountRepository = Mockito
            .mock(BankAccountRepository.class);
    private final BankAccountLocker bankAccountLocker = Mockito.mock(BankAccountLocker.class);
    private final TransactionManager transactionManager = Mockito.mock(TransactionManager.class);

    private final PayInterestInteractor payInterestService = new PayInterestInteractor(this.bankAccountLocker,
            this.bankAccountRepository, this.transactionManager);

    @Test
    void payInterestWithPositiveBalanceAndPositiveInterestRateInBankAccount() throws BankAccountNotFoundException {
        final long accountID = 1L;
        final long interestRate = 10L;
        final BigDecimal amount = BigDecimal.valueOf(100);

        final SavingsBankAccount bankAccount = new SavingsBankAccount(accountID, new Owner(1L, "Marcelo", "Chiaradia"),
                interestRate);
        bankAccount.deposit(amount);
        assertEquals(bankAccount.getBalance(), amount);

        given(this.bankAccountRepository.getByAccountID(eq(accountID))).willReturn(Optional.of(bankAccount));

        this.payInterestService.payInterest(accountID);

        then(this.transactionManager).should().beginTransaction();
        then(this.bankAccountLocker).should().lockBankAccountByID(eq(accountID));
        then(this.bankAccountRepository).should().save(eq(bankAccount));
        then(this.bankAccountLocker).should().unlockBankAccountByID(eq(accountID));
        then(this.transactionManager).should().commitTransaction();

        // interestRate * balance / 100
        final BigDecimal interest = BigDecimal.valueOf(interestRate).multiply(amount).divide(BigDecimal.valueOf(100));
        assertEquals(bankAccount.getBalance(), amount.add(interest));
    }

    @Test
    void payInterestWithNoBalanceInBankAccount() throws BankAccountNotFoundException {
        final long accountID = 1L;
        final long interestRate = 10L;

        final SavingsBankAccount bankAccount = new SavingsBankAccount(accountID, new Owner(1L, "Marcelo", "Chiaradia"),
                interestRate);
        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);

        given(this.bankAccountRepository.getByAccountID(eq(accountID))).willReturn(Optional.of(bankAccount));

        this.payInterestService.payInterest(accountID);

        then(this.bankAccountLocker).should().lockBankAccountByID(eq(accountID));
        then(this.bankAccountLocker).should().unlockBankAccountByID(eq(accountID));
        then(this.bankAccountRepository).should().save(eq(bankAccount));
        then(this.transactionManager).should().beginTransaction();
        then(this.transactionManager).should().commitTransaction();

        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);
    }

    @Test
    void payInterestBankAccountNotFound() {
        final long accountID = 1L;

        given(this.bankAccountRepository.getByAccountID(eq(accountID))).willReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class, () -> this.payInterestService.payInterest(accountID));

        then(this.transactionManager).should().beginTransaction();
        then(this.transactionManager).should().rollbackTransaction();
    }

}
