package gateway;

import java.util.Optional;

import entity.BankAccount;

public interface BankAccountRepository<T extends BankAccount> {

    Optional<T> getByAccountID(Long accountID);

    void save(T bankAccount);

}
