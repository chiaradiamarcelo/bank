package bank.application.usecases.withdraw;

import bank.application.domain.BankAccount;
import bank.application.domain.InsufficientFundsException;
import bank.application.fakes.FakeBankAccountRepository;
import bank.application.usecases.BankAccountNotFoundException;
import bank.application.usecases.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static bank.application.TestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WithdrawUseCaseTest {
    private BankAccountRepository bankAccountRepository;
    private WithdrawUseCase withdrawUseCase;

    @BeforeEach
    void setUp() {
        this.bankAccountRepository = new FakeBankAccountRepository();
        this.withdrawUseCase = new WithdrawUseCase(bankAccountRepository);
    }

    @Test
    void should_succeed_when_enough_money_in_bank_account() {
        bankAccountRepository.save(new BankAccount(BANK_ACCOUNT_ID, TEN_EUROS));

        withdrawUseCase.withdraw(new WithdrawRequest(BANK_ACCOUNT_ID, TEN_EUROS));

        var bankAccountAfterWithdraw = bankAccountRepository.accountById(BANK_ACCOUNT_ID).orElseThrow();
        assertThat(bankAccountAfterWithdraw).isEqualTo(new BankAccount(BANK_ACCOUNT_ID, ZERO_EUROS));
    }

    @Test
    void should_fail_when_bank_account_not_found() {
        assertThatThrownBy(() -> withdrawUseCase.withdraw(new WithdrawRequest(NON_EXISTENT_BANK_ACCOUNT_ID, TEN_EUROS)))
                .isInstanceOf(BankAccountNotFoundException.class);
    }

    @Test
    void should_fail_when_not_enough_money_in_bank_account() {
        bankAccountRepository.save(new BankAccount(BANK_ACCOUNT_ID, ZERO_EUROS));

        assertThatThrownBy(() -> withdrawUseCase.withdraw(new WithdrawRequest(BANK_ACCOUNT_ID, TEN_EUROS)))
                .isInstanceOf(InsufficientFundsException.class);
        assertThat(bankAccountRepository.accountById(BANK_ACCOUNT_ID).orElseThrow())
                .isEqualTo(new BankAccount(BANK_ACCOUNT_ID, ZERO_EUROS));
    }
}
