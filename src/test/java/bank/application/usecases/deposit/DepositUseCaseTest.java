package bank.application.usecases.deposit;

import bank.application.domain.BankAccount;
import bank.application.domain.InvalidAmountException;
import bank.application.usecases.BankAccountNotFoundException;
import bank.application.usecases.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static bank.application.TestFixtures.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class DepositUseCaseTest {
    private BankAccountRepository bankAccountRepository;
    private DepositUseCase depositUseCase;

    @BeforeEach
    void setUp() {
        this.bankAccountRepository = mock(BankAccountRepository.class);
        this.depositUseCase = new DepositUseCase(bankAccountRepository);
    }

    @Test
    void should_succeed_when_bank_account_exists_and_positive_amount_provided() {
        var bankAccount = mock(BankAccount.class);
        when(bankAccountRepository.accountById(eq(BANK_ACCOUNT_ID))).thenReturn(Optional.of(bankAccount));

        depositUseCase.deposit(new DepositRequest(BANK_ACCOUNT_ID, TEN_EUROS));

        verify(bankAccount, times(1)).deposit(eq(TEN_EUROS));
        verify(bankAccountRepository, times(1)).accountById(eq(BANK_ACCOUNT_ID));
        verify(bankAccountRepository, times(1)).save(eq(bankAccount));
    }

    @Test
    void should_fail_when_none_existent_bank_account() {
        when(bankAccountRepository.accountById(eq(NON_EXISTENT_BANK_ACCOUNT_ID)))
                .thenThrow(new BankAccountNotFoundException(NON_EXISTENT_BANK_ACCOUNT_ID));

        assertThatThrownBy(() -> depositUseCase.deposit(new DepositRequest(NON_EXISTENT_BANK_ACCOUNT_ID, TEN_EUROS)))
                .isInstanceOf(BankAccountNotFoundException.class);
        verify(bankAccountRepository, times(1)).accountById(eq(NON_EXISTENT_BANK_ACCOUNT_ID));
    }

    @Test
    void should_fail_when_negative_amount_provided() {
        var bankAccount = mock(BankAccount.class);
        var invalidDepositAmount = TEN_EUROS.negate();
        doThrow(InvalidAmountException.class).when(bankAccount).deposit(eq(invalidDepositAmount));
        when(bankAccountRepository.accountById(eq(BANK_ACCOUNT_ID))).thenReturn(Optional.of(bankAccount));

        assertThatThrownBy(() -> depositUseCase.deposit(new DepositRequest(BANK_ACCOUNT_ID, invalidDepositAmount)))
                .isInstanceOf(InvalidAmountException.class);
        verify(bankAccount, times(1)).deposit(eq(invalidDepositAmount));
        verify(bankAccountRepository, times(1)).accountById(eq(BANK_ACCOUNT_ID));
        verify(bankAccountRepository, times(0)).save(eq(bankAccount));
    }
}
