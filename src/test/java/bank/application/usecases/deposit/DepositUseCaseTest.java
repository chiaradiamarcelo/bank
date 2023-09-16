package bank.application.usecases.deposit;

import bank.application.domain.BankAccount;
import bank.application.domain.InvalidAmountException;
import bank.application.fakes.FakeBankAccountRepository;
import bank.application.usecases.BankAccountNotFoundException;
import bank.application.usecases.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static bank.application.TestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DepositUseCaseTest {
    private BankAccountRepository bankAccountRepository;
    private DepositUseCase depositUseCase;

    @BeforeEach
    void setUp() {
        this.bankAccountRepository = new FakeBankAccountRepository();
        this.depositUseCase = new DepositUseCase(bankAccountRepository);
    }

    @Test
    void should_succeed_when_bank_account_exists_and_positive_amount_provided() {
        bankAccountRepository.save(new BankAccount(BANK_ACCOUNT_ID, ZERO_EUROS));

        depositUseCase.deposit(new DepositRequest(BANK_ACCOUNT_ID, TEN_EUROS));

        var bankAccountAfterDeposit = bankAccountRepository.accountById(BANK_ACCOUNT_ID).orElseThrow();
        assertThat(bankAccountAfterDeposit).isEqualTo(new BankAccount(BANK_ACCOUNT_ID, TEN_EUROS));
    }

    @Test
    void should_fail_when_none_existent_bank_account() {
        assertThatThrownBy(() -> depositUseCase.deposit(new DepositRequest(NON_EXISTENT_BANK_ACCOUNT_ID, TEN_EUROS)))
                .isInstanceOf(BankAccountNotFoundException.class);
    }

    @Test
    void should_fail_when_negative_amount_provided() {
        bankAccountRepository.save(new BankAccount(BANK_ACCOUNT_ID, ZERO_EUROS));
        var invalidDepositAmount = TEN_EUROS.negate();

        assertThatThrownBy(() -> depositUseCase.deposit(new DepositRequest(BANK_ACCOUNT_ID, invalidDepositAmount)))
                .isInstanceOf(InvalidAmountException.class);
        assertThat(bankAccountRepository.accountById(BANK_ACCOUNT_ID).orElseThrow())
                .isEqualTo(new BankAccount(BANK_ACCOUNT_ID, ZERO_EUROS));
    }
}
