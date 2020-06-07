package usecase.transfer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import entity.CheckingBankAccount;
import entity.Owner;
import gateway.AccountLocker;
import gateway.BankAccountRepository;
import gateway.TransactionManager;
import usecase.exception.BankAccountNotFoundException;
import usecase.exception.InsufficientFundsException;

public class TransferInteractorTest {

    @SuppressWarnings("unchecked")
    private BankAccountRepository<CheckingBankAccount> bankAccountRepository =
            Mockito.mock(BankAccountRepository.class);
    private AccountLocker accountLocker = Mockito.mock(AccountLocker.class);
    private TransactionManager transactionManager = Mockito.mock(TransactionManager.class);

    private TransferInteractor transferService =
            new TransferInteractor(this.accountLocker, this.bankAccountRepository, this.transactionManager);

    @Test
    void transferSuccess_With_Positive_Balance_In_Origin_Bank_Account()
        throws BankAccountNotFoundException, InsufficientFundsException {
        long originAccountID = 1L;
        long destinationAccountID = 2L;
        BigDecimal amount = BigDecimal.valueOf(100);

        CheckingBankAccount originBankAccount =
                new CheckingBankAccount(originAccountID, new Owner(1L, "Marcelo", "Chiaradia"), BigDecimal.valueOf(0));
        originBankAccount.deposit(amount);
        assertEquals(originBankAccount.getBalance(), amount);

        CheckingBankAccount destinationBankAccount =
                new CheckingBankAccount(destinationAccountID, new Owner(2L, "Jhon", "Doe"), BigDecimal.valueOf(0));
        assertEquals(destinationBankAccount.getBalance(), BigDecimal.ZERO);

        given(this.bankAccountRepository.getByAccountID(eq(originAccountID)))
                .willReturn(Optional.of(originBankAccount));
        given(this.bankAccountRepository.getByAccountID(eq(destinationAccountID)))
                .willReturn(Optional.of(destinationBankAccount));

        this.transferService.transfer(originAccountID, destinationAccountID, amount);

        then(this.accountLocker).should().lockByAccountID(eq(originAccountID));
        then(this.accountLocker).should().unlockByAccountID(eq(originAccountID));
        then(this.accountLocker).should().lockByAccountID(eq(destinationAccountID));
        then(this.accountLocker).should().unlockByAccountID(eq(destinationAccountID));
        then(this.bankAccountRepository).should().save(eq(originBankAccount));
        then(this.bankAccountRepository).should().save(eq(destinationBankAccount));
        then(this.transactionManager).should().beginTransaction();
        then(this.transactionManager).should().commitTransaction();

        assertEquals(originBankAccount.getBalance(), BigDecimal.ZERO);
        assertEquals(destinationBankAccount.getBalance(), amount);
    }

    @Test
    void withdrawFailed_Insufficient_Funds_In_Origin_Bank_Account() throws BankAccountNotFoundException {
        long originAccountID = 1L;
        long destinationAccountID = 2L;
        BigDecimal amount = BigDecimal.valueOf(100);

        CheckingBankAccount originBankAccount =
                new CheckingBankAccount(originAccountID, new Owner(1L, "Marcelo", "Chiaradia"), BigDecimal.valueOf(0));
        assertEquals(originBankAccount.getBalance(), BigDecimal.ZERO);

        CheckingBankAccount destinationBankAccount =
                new CheckingBankAccount(destinationAccountID, new Owner(2L, "Jhon", "Doe"), BigDecimal.valueOf(0));
        assertEquals(destinationBankAccount.getBalance(), BigDecimal.ZERO);

        given(this.bankAccountRepository.getByAccountID(eq(originAccountID)))
                .willReturn(Optional.of(originBankAccount));
        given(this.bankAccountRepository.getByAccountID(eq(destinationAccountID)))
                .willReturn(Optional.of(destinationBankAccount));

        assertThrows(InsufficientFundsException.class,
                () -> this.transferService.transfer(originAccountID, destinationAccountID, amount));

        then(this.accountLocker).should().lockByAccountID(eq(originAccountID));
        then(this.accountLocker).should().unlockByAccountID(eq(originAccountID));
        then(this.accountLocker).should().lockByAccountID(eq(destinationAccountID));
        then(this.accountLocker).should().unlockByAccountID(eq(destinationAccountID));
        then(this.transactionManager).should().beginTransaction();
        then(this.transactionManager).should().rollbackTransaction();

        assertEquals(originBankAccount.getBalance(), BigDecimal.ZERO);
        assertEquals(destinationBankAccount.getBalance(), BigDecimal.ZERO);
    }

    @Test
    void withdrawFailed_Origin_Bank_Account_Not_Found() {
        long originAccountID = 1L;
        long destinationAccountID = 2L;
        BigDecimal amount = BigDecimal.valueOf(100);

        given(this.bankAccountRepository.getByAccountID(eq(originAccountID))).willReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class,
                () -> this.transferService.transfer(originAccountID, destinationAccountID, amount));

        then(this.transactionManager).should().beginTransaction();
        then(this.transactionManager).should().rollbackTransaction();
    }

    @Test
    void withdrawFailed_Destination_Bank_Account_Not_Found() {
        long originAccountID = 1L;
        long destinationAccountID = 2L;
        BigDecimal amount = BigDecimal.valueOf(100);

        CheckingBankAccount originBankAccount =
                new CheckingBankAccount(originAccountID, new Owner(1L, "Marcelo", "Chiaradia"), BigDecimal.valueOf(0));

        given(this.bankAccountRepository.getByAccountID(eq(originAccountID)))
                .willReturn(Optional.of(originBankAccount));
        given(this.bankAccountRepository.getByAccountID(eq(destinationAccountID))).willReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class,
                () -> this.transferService.transfer(originAccountID, destinationAccountID, amount));

        then(this.transactionManager).should().beginTransaction();
        then(this.transactionManager).should().rollbackTransaction();
    }

}
