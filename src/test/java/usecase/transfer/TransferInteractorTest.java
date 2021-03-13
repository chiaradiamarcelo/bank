package usecase.transfer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import entity.CheckingBankAccount;
import entity.Owner;
import gateway.*;
import usecase.exception.BankAccountNotFoundException;
import usecase.exception.InsufficientFundsException;

class TransferInteractorTest {
    @SuppressWarnings("unchecked")
    private final BankAccountRepository<CheckingBankAccount> bankAccountRepository = Mockito
            .mock(BankAccountRepository.class);
    private final BankAccountLocker bankAccountLocker = Mockito.mock(BankAccountLocker.class);
    private final TransactionManager transactionManager = Mockito.mock(TransactionManager.class);
    private final TransferInteractor transferService = new TransferInteractor(this.bankAccountLocker,
            this.bankAccountRepository, this.transactionManager);

    @Test
    void transferWithPositiveBalanceInOriginBankAccount()
            throws BankAccountNotFoundException, InsufficientFundsException {
        final long originAccountID = 1L;
        final long destinationAccountID = 2L;
        final BigDecimal amount = BigDecimal.valueOf(100);

        final CheckingBankAccount originBankAccount = new CheckingBankAccount(originAccountID,
                new Owner(1L, "Marcelo", "Chiaradia"), BigDecimal.ZERO);
        originBankAccount.deposit(amount);
        assertEquals(amount, originBankAccount.getBalance());

        final CheckingBankAccount destinationBankAccount = new CheckingBankAccount(destinationAccountID,
                new Owner(2L, "Jhon", "Doe"), BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, destinationBankAccount.getBalance());

        given(this.bankAccountRepository.getByAccountID(originAccountID)).willReturn(Optional.of(originBankAccount));
        given(this.bankAccountRepository.getByAccountID(destinationAccountID))
                .willReturn(Optional.of(destinationBankAccount));

        this.transferService.transfer(originAccountID, destinationAccountID, amount);

        then(this.bankAccountLocker).should().lockBankAccountByID(originAccountID);
        then(this.bankAccountLocker).should().unlockBankAccountByID(originAccountID);
        then(this.bankAccountLocker).should().lockBankAccountByID(destinationAccountID);
        then(this.bankAccountLocker).should().unlockBankAccountByID(destinationAccountID);
        then(this.bankAccountRepository).should().save(originBankAccount);
        then(this.bankAccountRepository).should().save(destinationBankAccount);
        assertTransactionWasCommited();

        assertEquals(BigDecimal.ZERO, originBankAccount.getBalance());
        assertEquals(amount, destinationBankAccount.getBalance());
    }

    private void assertTransactionWasCommited() {
        then(this.transactionManager).should().beginTransaction();
        then(this.transactionManager).should().commitTransaction();
    }

    @Test
    void withdrawInsufficientFundsInOriginBankAccount() throws BankAccountNotFoundException {
        final long originAccountID = 1L;
        final long destinationAccountID = 2L;
        final BigDecimal amount = BigDecimal.valueOf(100);

        final CheckingBankAccount originBankAccount = new CheckingBankAccount(originAccountID,
                new Owner(1L, "Marcelo", "Chiaradia"), BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, originBankAccount.getBalance());

        final CheckingBankAccount destinationBankAccount = new CheckingBankAccount(destinationAccountID,
                new Owner(2L, "Jhon", "Doe"), BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, destinationBankAccount.getBalance());

        given(this.bankAccountRepository.getByAccountID(originAccountID)).willReturn(Optional.of(originBankAccount));
        given(this.bankAccountRepository.getByAccountID(destinationAccountID))
                .willReturn(Optional.of(destinationBankAccount));

        assertThrows(InsufficientFundsException.class,
                () -> this.transferService.transfer(originAccountID, destinationAccountID, amount));

        then(this.bankAccountLocker).should().lockBankAccountByID(originAccountID);
        then(this.bankAccountLocker).should().unlockBankAccountByID(originAccountID);
        then(this.bankAccountLocker).should().lockBankAccountByID(destinationAccountID);
        then(this.bankAccountLocker).should().unlockBankAccountByID(destinationAccountID);
        assertTransactionWasRollbacked();

        assertEquals(BigDecimal.ZERO, originBankAccount.getBalance());
        assertEquals(BigDecimal.ZERO, destinationBankAccount.getBalance());
    }

    private void assertTransactionWasRollbacked() {
        then(this.transactionManager).should().beginTransaction();
        then(this.transactionManager).should().rollbackTransaction();
    }

    @Test
    void withdrawOriginBankAccountNotFound() {
        final long originAccountID = 1L;
        final long destinationAccountID = 2L;
        final BigDecimal amount = BigDecimal.valueOf(100);

        given(this.bankAccountRepository.getByAccountID(originAccountID)).willReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class,
                () -> this.transferService.transfer(originAccountID, destinationAccountID, amount));

        assertTransactionWasRollbacked();
    }

    @Test
    void withdrawDestinationBankAccountNotFound() {
        final long originAccountID = 1L;
        final long destinationAccountID = 2L;
        final BigDecimal amount = BigDecimal.valueOf(100);

        final CheckingBankAccount originBankAccount = new CheckingBankAccount(originAccountID,
                new Owner(1L, "Marcelo", "Chiaradia"), BigDecimal.ZERO);

        given(this.bankAccountRepository.getByAccountID(originAccountID)).willReturn(Optional.of(originBankAccount));
        given(this.bankAccountRepository.getByAccountID(destinationAccountID)).willReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class,
                () -> this.transferService.transfer(originAccountID, destinationAccountID, amount));

        assertTransactionWasRollbacked();
    }
}
