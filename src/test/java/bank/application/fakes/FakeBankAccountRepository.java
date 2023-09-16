package bank.application.fakes;

import bank.application.domain.BankAccount;
import bank.application.usecases.BankAccountRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FakeBankAccountRepository implements BankAccountRepository {
    private final Map<UUID, BankAccount> bankAccounts;

    public FakeBankAccountRepository() {
        this.bankAccounts = new HashMap<>();
    }

    @Override
    public Optional<BankAccount> accountById(UUID accountId) {
        return Optional.ofNullable(bankAccounts.get(accountId))
                .map(found -> new BankAccount(found.id(), found.balance()));
    }

    @Override
    public void save(BankAccount bankAccount) {
        bankAccounts.put(bankAccount.id(), new BankAccount(bankAccount.id(), bankAccount.balance()));
    }

    @Override
    public void updateBalance(UUID accountId, BigDecimal balance) {
        if (bankAccounts.containsKey(accountId)) {
            bankAccounts.put(accountId, new BankAccount(accountId, balance));
        }
    }
}
