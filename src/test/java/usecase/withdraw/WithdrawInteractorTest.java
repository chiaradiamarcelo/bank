package usecase.withdraw;

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
import usecase.exception.InsufficientFundsException;

public class WithdrawInteractorTest {
    @SuppressWarnings("unchecked")
    private final BankAccountRepository<BankAccount> bankAccountRepository = Mockito.mock(BankAccountRepository.class);
    private final BankAccountLocker bankAccountLocker = Mockito.mock(BankAccountLocker.class);
    private final TransactionManager transactionManager = Mockito.mock(TransactionManager.class);

    private final WithdrawInteractor withdrawService = new WithdrawInteractor(this.bankAccountLocker,
            this.bankAccountRepository, this.transactionManager);

    @Test
    void withdrawWithPositiveBalanceInBankAccount() throws BankAccountNotFoundException, InsufficientFundsException {
        final long accountID = 1L;
        final BigDecimal amount = BigDecimal.valueOf(100);
        final BankAccount bankAccount = Mockito.mock(BankAccount.class,
                Mockito.withSettings().useConstructor(accountID, new Owner(1L, "Marcelo", "Chiaradia"))
                        .defaultAnswer(Mockito.CALLS_REAL_METHODS));
        bankAccount.deposit(amount);
        assertEquals(bankAccount.getBalance(), amount);

        given(this.bankAccountRepository.getByAccountID(eq(accountID))).willReturn(Optional.of(bankAccount));
        given(bankAccount.mayWithdraw(eq(amount))).willReturn(true);

        this.withdrawService.withdraw(accountID, amount);

        then(this.bankAccountLocker).should().lockBankAccountByID(eq(accountID));
        then(this.bankAccountLocker).should().unlockBankAccountByID(eq(accountID));
        then(this.bankAccountRepository).should().save(eq(bankAccount));
        then(this.transactionManager).should().beginTransaction();
        then(this.transactionManager).should().commitTransaction();

        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);
    }

    @Test
    void withdrawInsufficientFundsInBankAccount() throws BankAccountNotFoundException {
        final long accountID = 1L;
        final BigDecimal amount = BigDecimal.valueOf(100);
        final BankAccount bankAccount = Mockito.mock(BankAccount.class,
                Mockito.withSettings().useConstructor(accountID, new Owner(1L, "Marcelo", "Chiaradia"))
                        .defaultAnswer(Mockito.CALLS_REAL_METHODS));
        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);

        given(this.bankAccountRepository.getByAccountID(eq(accountID))).willReturn(Optional.of(bankAccount));
        given(bankAccount.mayWithdraw(eq(amount))).willReturn(false);

        assertThrows(InsufficientFundsException.class, () -> this.withdrawService.withdraw(accountID, amount));

        then(this.bankAccountLocker).should().lockBankAccountByID(eq(accountID));
        then(this.bankAccountLocker).should().unlockBankAccountByID(eq(accountID));
        then(this.transactionManager).should().beginTransaction();
        then(this.transactionManager).should().rollbackTransaction();

        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);
    }

    @Test
    void withdrawBankAccountNotFound() {
        final long accountID = 1L;
        final BigDecimal depositAmount = BigDecimal.valueOf(100);

        given(this.bankAccountRepository.getByAccountID(eq(accountID))).willReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class, () -> this.withdrawService.withdraw(accountID, depositAmount));

        then(this.transactionManager).should().beginTransaction();
        then(this.transactionManager).should().rollbackTransaction();
    }
}
