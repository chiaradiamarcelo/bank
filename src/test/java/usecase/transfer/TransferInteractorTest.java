package usecase.transfer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
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

public class TransferInteractorTest {
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
                new Owner(1L, "Marcelo", "Chiaradia"), BigDecimal.valueOf(0));
        originBankAccount.deposit(amount);
        assertEquals(originBankAccount.getBalance(), amount);

        final CheckingBankAccount destinationBankAccount = new CheckingBankAccount(destinationAccountID,
                new Owner(2L, "Jhon", "Doe"), BigDecimal.valueOf(0));
        assertEquals(destinationBankAccount.getBalance(), BigDecimal.ZERO);

        given(this.bankAccountRepository.getByAccountID(eq(originAccountID)))
                .willReturn(Optional.of(originBankAccount));
        given(this.bankAccountRepository.getByAccountID(eq(destinationAccountID)))
                .willReturn(Optional.of(destinationBankAccount));

        this.transferService.transfer(originAccountID, destinationAccountID, amount);

        then(this.bankAccountLocker).should().lockBankAccountByID(eq(originAccountID));
        then(this.bankAccountLocker).should().unlockBankAccountByID(eq(originAccountID));
        then(this.bankAccountLocker).should().lockBankAccountByID(eq(destinationAccountID));
        then(this.bankAccountLocker).should().unlockBankAccountByID(eq(destinationAccountID));
        then(this.bankAccountRepository).should().save(eq(originBankAccount));
        then(this.bankAccountRepository).should().save(eq(destinationBankAccount));
        then(this.transactionManager).should().beginTransaction();
        then(this.transactionManager).should().commitTransaction();

        assertEquals(originBankAccount.getBalance(), BigDecimal.ZERO);
        assertEquals(destinationBankAccount.getBalance(), amount);
    }

    @Test
    void withdrawInsufficientFundsInOriginBankAccount() throws BankAccountNotFoundException {
        final long originAccountID = 1L;
        final long destinationAccountID = 2L;
        final BigDecimal amount = BigDecimal.valueOf(100);

        final CheckingBankAccount originBankAccount = new CheckingBankAccount(originAccountID,
                new Owner(1L, "Marcelo", "Chiaradia"), BigDecimal.valueOf(0));
        assertEquals(originBankAccount.getBalance(), BigDecimal.ZERO);

        final CheckingBankAccount destinationBankAccount = new CheckingBankAccount(destinationAccountID,
                new Owner(2L, "Jhon", "Doe"), BigDecimal.valueOf(0));
        assertEquals(destinationBankAccount.getBalance(), BigDecimal.ZERO);

        given(this.bankAccountRepository.getByAccountID(eq(originAccountID)))
                .willReturn(Optional.of(originBankAccount));
        given(this.bankAccountRepository.getByAccountID(eq(destinationAccountID)))
                .willReturn(Optional.of(destinationBankAccount));

        assertThrows(InsufficientFundsException.class,
                () -> this.transferService.transfer(originAccountID, destinationAccountID, amount));

        then(this.bankAccountLocker).should().lockBankAccountByID(eq(originAccountID));
        then(this.bankAccountLocker).should().unlockBankAccountByID(eq(originAccountID));
        then(this.bankAccountLocker).should().lockBankAccountByID(eq(destinationAccountID));
        then(this.bankAccountLocker).should().unlockBankAccountByID(eq(destinationAccountID));
        then(this.transactionManager).should().beginTransaction();
        then(this.transactionManager).should().rollbackTransaction();

        assertEquals(originBankAccount.getBalance(), BigDecimal.ZERO);
        assertEquals(destinationBankAccount.getBalance(), BigDecimal.ZERO);
    }

    @Test
    void withdrawOriginBankAccountNotFound() {
        final long originAccountID = 1L;
        final long destinationAccountID = 2L;
        final BigDecimal amount = BigDecimal.valueOf(100);

        given(this.bankAccountRepository.getByAccountID(eq(originAccountID))).willReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class,
                () -> this.transferService.transfer(originAccountID, destinationAccountID, amount));

        then(this.transactionManager).should().beginTransaction();
        then(this.transactionManager).should().rollbackTransaction();
    }

    @Test
    void withdrawDestinationBankAccountNotFound() {
        final long originAccountID = 1L;
        final long destinationAccountID = 2L;
        final BigDecimal amount = BigDecimal.valueOf(100);

        final CheckingBankAccount originBankAccount = new CheckingBankAccount(originAccountID,
                new Owner(1L, "Marcelo", "Chiaradia"), BigDecimal.valueOf(0));

        given(this.bankAccountRepository.getByAccountID(eq(originAccountID)))
                .willReturn(Optional.of(originBankAccount));
        given(this.bankAccountRepository.getByAccountID(eq(destinationAccountID))).willReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class,
                () -> this.transferService.transfer(originAccountID, destinationAccountID, amount));

        then(this.transactionManager).should().beginTransaction();
        then(this.transactionManager).should().rollbackTransaction();
    }

}
