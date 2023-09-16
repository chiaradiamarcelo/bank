package bank.application.usecases.withdraw;

import bank.application.domain.BankAccount;
import bank.application.domain.InsufficientFundsException;
import bank.application.usecases.BankAccountNotFoundException;
import bank.application.usecases.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static bank.application.TestFixtures.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class WithdrawUseCaseTest {
    private BankAccountRepository bankAccountRepository;
    private WithdrawUseCase withdrawUseCase;

    @BeforeEach
    void setUp() {
        this.bankAccountRepository = mock(BankAccountRepository.class);
        this.withdrawUseCase = new WithdrawUseCase(bankAccountRepository);
    }

    @Test
    void should_succeed_when_enough_money_in_bank_account() {
        var bankAccount = mock(BankAccount.class);
        when(bankAccountRepository.accountById(eq(BANK_ACCOUNT_ID))).thenReturn(Optional.of(bankAccount));

        withdrawUseCase.withdraw(new WithdrawRequest(BANK_ACCOUNT_ID, TEN_EUROS));

        verify(bankAccount, times(1)).withdraw(eq(TEN_EUROS));
        verify(bankAccountRepository, times(1)).accountById(eq(BANK_ACCOUNT_ID));
        verify(bankAccountRepository, times(1)).save(eq(bankAccount));
    }

    @Test
    void should_fail_when_bank_account_not_found() {
        when(bankAccountRepository.accountById(eq(NON_EXISTENT_BANK_ACCOUNT_ID)))
                .thenThrow(new BankAccountNotFoundException(NON_EXISTENT_BANK_ACCOUNT_ID));

        assertThatThrownBy(() -> withdrawUseCase.withdraw(new WithdrawRequest(NON_EXISTENT_BANK_ACCOUNT_ID, TEN_EUROS)))
                .isInstanceOf(BankAccountNotFoundException.class);
        verify(bankAccountRepository, times(1)).accountById(eq(NON_EXISTENT_BANK_ACCOUNT_ID));
    }

    @Test
    void should_fail_when_not_enough_money_in_bank_account() {
        var bankAccount = mock(BankAccount.class);
        doThrow(InsufficientFundsException.class).when(bankAccount).withdraw(eq(TEN_EUROS));
        when(bankAccountRepository.accountById(eq(BANK_ACCOUNT_ID))).thenReturn(Optional.of(bankAccount));

        assertThatThrownBy(() -> withdrawUseCase.withdraw(new WithdrawRequest(BANK_ACCOUNT_ID, TEN_EUROS)))
                .isInstanceOf(InsufficientFundsException.class);
        verify(bankAccount, times(1)).withdraw(eq(TEN_EUROS));
        verify(bankAccountRepository, times(1)).accountById(eq(BANK_ACCOUNT_ID));
        verify(bankAccountRepository, times(0)).save(eq(bankAccount));
    }
}
