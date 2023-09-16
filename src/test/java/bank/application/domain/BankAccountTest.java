package bank.application.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static bank.application.TestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

class BankAccountTest {
    @Test
    void should_assert_equality() {
        var bankAccount = new BankAccount(BANK_ACCOUNT_ID, TEN_EUROS);

        assertThat(bankAccount).isEqualTo(new BankAccount(BANK_ACCOUNT_ID, TEN_EUROS));
        assertThat(bankAccount).isNotEqualTo(new BankAccount(BANK_ACCOUNT_ID, ZERO_EUROS));
        assertThat(bankAccount).isNotEqualTo(new BankAccount(UUID.randomUUID(), TEN_EUROS));
    }
}
