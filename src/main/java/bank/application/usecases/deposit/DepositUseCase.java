package bank.application.usecases.deposit;

import bank.application.domain.BankAccount;
import bank.application.usecases.BankAccountNotFoundException;
import bank.application.usecases.BankAccountRepository;

import java.util.UUID;

public class DepositUseCase {
    private final BankAccountRepository bankAccountRepository;

    public DepositUseCase(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    public void deposit(DepositRequest request) {
        var bankAccount = bankAccount(request.accountId());
        bankAccount.deposit(request.amount());
        bankAccountRepository.save(bankAccount);
    }

    private BankAccount bankAccount(UUID accountId) {
        return bankAccountRepository.accountById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException(accountId));
    }
}
