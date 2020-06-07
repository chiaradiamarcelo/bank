package usecase.deposit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import entity.BankAccount;
import entity.CheckingBankAccount;
import entity.Owner;
import gateway.AccountLocker;
import gateway.BankAccountRepository;
import gateway.TransactionManager;
import usecase.exception.BankAccountNotFoundException;

public class DepositInteractorTest {

    @SuppressWarnings("unchecked")
    private BankAccountRepository<BankAccount> bankAccountRepository = Mockito.mock(BankAccountRepository.class);
    private AccountLocker accountLocker = Mockito.mock(AccountLocker.class);
    private TransactionManager transactionManager = Mockito.mock(TransactionManager.class);

    private DepositInteractor depositService =
            new DepositInteractor(this.accountLocker, this.bankAccountRepository, this.transactionManager);

    @Test
    void depositSuccess() throws BankAccountNotFoundException {
        long accountID = 1L;
        BigDecimal amount = BigDecimal.valueOf(100);
        BankAccount bankAccount = Mockito.mock(BankAccount.class,
                Mockito.withSettings().useConstructor(accountID, new Owner(1L, "Marcelo", "Chiaradia"))
                        .defaultAnswer(Mockito.CALLS_REAL_METHODS));
        assertEquals(bankAccount.getBalance(), BigDecimal.ZERO);

        given(this.bankAccountRepository.getByAccountID(eq(accountID))).willReturn(Optional.of(bankAccount));

        this.depositService.deposit(accountID, amount);

        then(this.accountLocker).should().lockByAccountID(eq(accountID));
        then(this.bankAccountRepository).should().save(eq(bankAccount));
        then(this.accountLocker).should().unlockByAccountID(eq(accountID));
        then(this.transactionManager).should().beginTransaction();
        then(this.transactionManager).should().commitTransaction();

        assertEquals(bankAccount.getBalance(), amount);
    }

    @Test
    void depositFailed_Bank_Account_Not_Found() {
        long accountID = 1L;
        BigDecimal amount = BigDecimal.valueOf(100);

        given(this.bankAccountRepository.getByAccountID(eq(accountID))).willReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class, () -> this.depositService.deposit(accountID, amount));

        then(this.transactionManager).should().beginTransaction();
        then(this.transactionManager).should().rollbackTransaction();
    }

}
