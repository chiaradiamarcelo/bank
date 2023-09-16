package bank.application.usecases.withdraw;

import bank.application.domain.BankAccount;
import bank.application.usecases.BankAccountNotFoundException;
import bank.application.usecases.BankAccountRepository;

import java.util.UUID;

public class WithdrawUseCase {
    private final BankAccountRepository bankAccountRepository;

    public WithdrawUseCase(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    public void withdraw(WithdrawRequest request) {
        var bankAccount = bankAccount(request.accountId());
        bankAccount.withdraw(request.amount());
        bankAccountRepository.save(bankAccount);
    }

    private BankAccount bankAccount(UUID accountId) {
        return bankAccountRepository.accountById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException(accountId));
    }
}
