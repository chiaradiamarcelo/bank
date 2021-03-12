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

class PayInterestInteractorTest {
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
        assertEquals(amount, bankAccount.getBalance());

        given(this.bankAccountRepository.getByAccountID(accountID)).willReturn(Optional.of(bankAccount));

        this.payInterestService.payInterest(accountID);

        then(this.transactionManager).should().beginTransaction();
        then(this.bankAccountLocker).should().lockBankAccountByID(accountID);
        then(this.bankAccountRepository).should().save(eq(bankAccount));
        then(this.bankAccountLocker).should().unlockBankAccountByID(accountID);
        then(this.transactionManager).should().commitTransaction();

        // interestRate * balance / 100
        final BigDecimal interest = BigDecimal.valueOf(interestRate).multiply(amount).divide(BigDecimal.valueOf(100));
        assertEquals(amount.add(interest), bankAccount.getBalance());
    }

    @Test
    void payInterestWithNoBalanceInBankAccount() throws BankAccountNotFoundException {
        final long accountID = 1L;
        final long interestRate = 10L;

        final SavingsBankAccount bankAccount = new SavingsBankAccount(accountID, new Owner(1L, "Marcelo", "Chiaradia"),
                interestRate);
        assertEquals(BigDecimal.ZERO, bankAccount.getBalance());

        given(this.bankAccountRepository.getByAccountID(accountID)).willReturn(Optional.of(bankAccount));

        this.payInterestService.payInterest(accountID);

        then(this.bankAccountLocker).should().lockBankAccountByID(accountID);
        then(this.bankAccountLocker).should().unlockBankAccountByID(accountID);
        then(this.bankAccountRepository).should().save(eq(bankAccount));
        then(this.transactionManager).should().beginTransaction();
        then(this.transactionManager).should().commitTransaction();

        assertEquals(BigDecimal.ZERO, bankAccount.getBalance());
    }

    @Test
    void payInterestBankAccountNotFound() {
        final long accountID = 1L;

        given(this.bankAccountRepository.getByAccountID(accountID)).willReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class, () -> this.payInterestService.payInterest(accountID));

        then(this.transactionManager).should().beginTransaction();
        then(this.transactionManager).should().rollbackTransaction();
    }
}
