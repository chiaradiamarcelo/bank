package usecase.payinterest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        final SavingsBankAccount bankAccount = this.getSavingsBankAccountWith(accountID, interestRate);
        bankAccount.deposit(amount);
        assertEquals(amount, bankAccount.getBalance());

        given(this.bankAccountRepository.getByAccountID(accountID)).willReturn(Optional.of(bankAccount));

        this.payInterestService.payInterest(accountID);

        then(this.bankAccountLocker).should().lockBankAccountByID(accountID);
        then(this.bankAccountLocker).should().unlockBankAccountByID(accountID);
        then(this.bankAccountRepository).should().save(bankAccount);
        assertTransactionWasCommited();

        // interestRate * balance / 100
        final BigDecimal interest = BigDecimal.valueOf(interestRate).multiply(amount).divide(BigDecimal.valueOf(100));
        assertEquals(amount.add(interest), bankAccount.getBalance());
    }

    private void assertTransactionWasCommited() {
        then(this.transactionManager).should().beginTransaction();
        then(this.transactionManager).should().commitTransaction();
    }

    private SavingsBankAccount getSavingsBankAccountWith(final long accountID, final long interestRate) {
        return new SavingsBankAccount(accountID, new Owner(1L, "Marcelo", "Chiaradia"), interestRate);
    }

    @Test
    void payInterestWithNoBalanceInBankAccount() throws BankAccountNotFoundException {
        final long accountID = 1L;
        final long interestRate = 10L;
        final SavingsBankAccount bankAccount = getSavingsBankAccountWith(accountID, interestRate);

        given(this.bankAccountRepository.getByAccountID(accountID)).willReturn(Optional.of(bankAccount));

        this.payInterestService.payInterest(accountID);

        then(this.bankAccountLocker).should().lockBankAccountByID(accountID);
        then(this.bankAccountLocker).should().unlockBankAccountByID(accountID);
        then(this.bankAccountRepository).should().save(bankAccount);
        assertTransactionWasCommited();

        assertEquals(BigDecimal.ZERO, bankAccount.getBalance());
    }

    @Test
    void payInterestWithBankAccountNotFound() {
        final long accountID = 1L;

        given(this.bankAccountRepository.getByAccountID(accountID)).willReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class, () -> this.payInterestService.payInterest(accountID));
        assertTransactionWasRollbacked();
    }

    private void assertTransactionWasRollbacked() {
        then(this.transactionManager).should().beginTransaction();
        then(this.transactionManager).should().rollbackTransaction();
    }
}
