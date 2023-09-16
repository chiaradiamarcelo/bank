package bank.application.contract;

import bank.application.domain.BankAccount;
import bank.application.usecases.BankAccountRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static bank.application.TestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class BankAccountRepositoryTest {
    @Test
    void should_insert_bank_account() {
        var bankAccount = new BankAccount(BANK_ACCOUNT_ID, TEN_EUROS);

        repository().save(bankAccount);

        assertThat(accountById(BANK_ACCOUNT_ID)).isEqualTo(new BankAccount(BANK_ACCOUNT_ID, TEN_EUROS));
    }

    @Test
    void should_update_bank_account() {
        repository().save(new BankAccount(BANK_ACCOUNT_ID, ZERO_EUROS));

        repository().save(new BankAccount(BANK_ACCOUNT_ID, TEN_EUROS));

        assertThat(accountById(BANK_ACCOUNT_ID)).isEqualTo(new BankAccount(BANK_ACCOUNT_ID, TEN_EUROS));
    }

    @Test
    void should_update_bank_account_balance() {
        repository().save(new BankAccount(BANK_ACCOUNT_ID, ZERO_EUROS));

        repository().updateBalance(BANK_ACCOUNT_ID, TEN_EUROS);

        assertThat(accountById(BANK_ACCOUNT_ID)).isEqualTo(new BankAccount(BANK_ACCOUNT_ID, TEN_EUROS));
    }

    @Test
    void should_retrieve_empty_when_getting_bank_account_by_id_and_none_found() {
        assertThat(repository().accountById(NON_EXISTENT_BANK_ACCOUNT_ID)).isEmpty();
    }

    @Test
    void should_retrieve_bank_account_by_id() {
        repository().save(new BankAccount(BANK_ACCOUNT_ID, ZERO_EUROS));

        assertThat(repository().accountById(BANK_ACCOUNT_ID).orElseThrow())
                .isEqualTo(new BankAccount(BANK_ACCOUNT_ID, ZERO_EUROS));
    }

    @Test
    void should_not_track_changes_in_bank_account_after_saving() {
        var bankAccount = new BankAccount(BANK_ACCOUNT_ID, ZERO_EUROS);
        repository().save(bankAccount);
        bankAccount.deposit(TEN_EUROS);

        assertThat(accountById(BANK_ACCOUNT_ID)).isEqualTo(new BankAccount(BANK_ACCOUNT_ID, ZERO_EUROS));
    }

    @Test
    void should_not_track_changes_in_bank_account_after_getting_bank_account_by_Id() {
        repository().save(new BankAccount(BANK_ACCOUNT_ID, ZERO_EUROS));
        var foundBankAccount = repository().accountById(BANK_ACCOUNT_ID).orElseThrow();
        foundBankAccount.deposit(TEN_EUROS);

        assertThat(accountById(BANK_ACCOUNT_ID)).isEqualTo(new BankAccount(BANK_ACCOUNT_ID, ZERO_EUROS));
    }

    private BankAccount accountById(UUID accountId) {
        return repository().accountById(accountId).orElseThrow();
    }

    protected abstract BankAccountRepository repository();
}
