package bank.application.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static bank.application.TestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankAccountTest {
    @Test
    void should_deposit() {
        var bankAccount = new BankAccount(BANK_ACCOUNT_ID, ZERO_EUROS);

        bankAccount.deposit(TEN_EUROS);

        assertThat(bankAccount).isEqualTo(new BankAccount(BANK_ACCOUNT_ID, TEN_EUROS));
    }

    @Test
    void should_fail_when_deposit_negative_amount() {
        var bankAccount = new BankAccount(BANK_ACCOUNT_ID, ZERO_EUROS);
        var invalidDepositAmount = TEN_EUROS.negate();

        assertThatThrownBy(() -> bankAccount.deposit(invalidDepositAmount)).isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void should_withdraw() {
        var bankAccount = new BankAccount(BANK_ACCOUNT_ID, TEN_EUROS);

        bankAccount.withdraw(TEN_EUROS);

        assertThat(bankAccount).isEqualTo(new BankAccount(BANK_ACCOUNT_ID, ZERO_EUROS));
    }

    @Test
    void should_fail_when_withdrawing_and_not_enough_money_in_bank_account() {
        var bankAccount = new BankAccount(BANK_ACCOUNT_ID, ZERO_EUROS);

        assertThatThrownBy(() -> bankAccount.withdraw(TEN_EUROS)).isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void should_assert_equality() {
        var bankAccount = new BankAccount(BANK_ACCOUNT_ID, TEN_EUROS);

        assertThat(bankAccount).isEqualTo(new BankAccount(BANK_ACCOUNT_ID, TEN_EUROS));
        assertThat(bankAccount).isNotEqualTo(new BankAccount(BANK_ACCOUNT_ID, ZERO_EUROS));
        assertThat(bankAccount).isNotEqualTo(new BankAccount(UUID.randomUUID(), TEN_EUROS));
    }
}