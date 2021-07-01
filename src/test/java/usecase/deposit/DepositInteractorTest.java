package usecase.deposit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import entity.BankAccount;
import entity.Owner;
import gateway.*;
import usecase.exception.BankAccountNotFoundException;

class DepositInteractorTest {
    @SuppressWarnings("unchecked")
    private final BankAccountRepository<BankAccount> bankAccountRepository = Mockito.mock(BankAccountRepository.class);
    private final BankAccountLocker bankAccountLocker = Mockito.mock(BankAccountLocker.class);
    private final TransactionManager transactionManager = Mockito.mock(TransactionManager.class);
    private final DepositInteractor depositService = new DepositInteractor(this.bankAccountLocker,
            this.bankAccountRepository, this.transactionManager);

    @Test
    void deposit() throws BankAccountNotFoundException {
        final long accountID = 1L;
        final BigDecimal amount = BigDecimal.valueOf(100);
        final BankAccount bankAccount = this.getBankAccountWith(accountID);

        given(this.bankAccountRepository.getByAccountID(accountID)).willReturn(Optional.of(bankAccount));

        this.depositService.deposit(accountID, amount);

        then(this.bankAccountLocker).should().lockBankAccountByID(eq(accountID));
        then(this.bankAccountLocker).should().unlockBankAccountByID(eq(accountID));
        then(this.bankAccountRepository).should().save(bankAccount);
        assertTransactionWasCommited();
        assertEquals(amount, bankAccount.getBalance());
    }

    private void assertTransactionWasCommited() {
        then(this.transactionManager).should().beginTransaction();
        then(this.transactionManager).should().commitTransaction();
    }

    private BankAccount getBankAccountWith(final long accountID) {
        return Mockito.mock(BankAccount.class,
                Mockito.withSettings().useConstructor(accountID, new Owner(1L, "Marcelo", "Chiaradia"))
                        .defaultAnswer(Mockito.CALLS_REAL_METHODS));
    }

    @Test
    void depositWithBankAccountNotFound() {
        final long accountID = 1L;
        final BigDecimal amount = BigDecimal.valueOf(100);

        given(this.bankAccountRepository.getByAccountID(accountID)).willReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class, () -> this.depositService.deposit(accountID, amount));
    }

    @Test
    void depositNegativeAmountFails() {
        final long accountID = 1L;
        final BigDecimal amount = BigDecimal.valueOf(-1);
        final BankAccount bankAccount = this.getBankAccountWith(accountID);
        given(this.bankAccountRepository.getByAccountID(accountID)).willReturn(Optional.of(bankAccount));
        assertThrows(IllegalArgumentException.class, () -> this.depositService.deposit(accountID, amount));
        assertTransactionWasRollbacked();
    }

    private void assertTransactionWasRollbacked() {
        then(this.transactionManager).should().beginTransaction();
        then(this.transactionManager).should().rollbackTransaction();
    }
}
