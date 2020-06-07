package usecase.deposit;

import java.math.BigDecimal;

import entity.BankAccount;
import gateway.AccountLocker;
import gateway.BankAccountRepository;
import gateway.TransactionManager;
import usecase.exception.BankAccountNotFoundException;

public class DepositInteractor implements DepositUseCase {

    private AccountLocker accountLocker;
    private BankAccountRepository<BankAccount> bankAccountRepository;
    private TransactionManager transactionManager;

    public DepositInteractor(AccountLocker accountLocker, BankAccountRepository<BankAccount> bankAccountRepository,
            TransactionManager transactionManager) {
        super();
        this.accountLocker = accountLocker;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    public void deposit(Long accountID, BigDecimal amount) throws BankAccountNotFoundException {
        this.transactionManager.beginTransaction();
        try {
            BankAccount bankAccount = this.bankAccountRepository.getByAccountID(accountID)
                    .orElseThrow(() -> new BankAccountNotFoundException(accountID));
            this.accountLocker.lockByAccountID(accountID);
            try {
                bankAccount.deposit(amount);
                this.bankAccountRepository.save(bankAccount);
                this.transactionManager.commitTransaction();
            }
            finally {
                this.accountLocker.unlockByAccountID(accountID);
            }
        }
        catch (Exception e) {
            this.transactionManager.rollbackTransaction();
            throw e;
        }
    }

}
