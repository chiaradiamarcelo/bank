package bank.application.usecases;

import bank.application.domain.BankAccount;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface BankAccountRepository {
    Optional<BankAccount> accountById(UUID accountId);

    void save(BankAccount bankAccount);

    void updateBalance(UUID accountId, BigDecimal balance);
}