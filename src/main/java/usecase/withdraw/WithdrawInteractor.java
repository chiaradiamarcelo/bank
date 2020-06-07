package usecase.withdraw;

import java.math.BigDecimal;

import entity.BankAccount;
import gateway.AccountLocker;
import gateway.BankAccountRepository;
import gateway.TransactionManager;
import usecase.exception.BankAccountNotFoundException;
import usecase.exception.InsufficientFundsException;

public class WithdrawInteractor implements WithdrawUseCase {

    private AccountLocker accountLocker;
    private BankAccountRepository<BankAccount> bankAccountRepository;
    private TransactionManager transactionManager;

    public WithdrawInteractor(AccountLocker accountLocker, BankAccountRepository<BankAccount> bankAccountRepository,
            TransactionManager transactionManager) {
        super();
        this.accountLocker = accountLocker;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    public void withdraw(Long accountID, BigDecimal amount)
        throws BankAccountNotFoundException, InsufficientFundsException {
        this.transactionManager.beginTransaction();
        try {
            BankAccount bankAccount = this.bankAccountRepository.getByAccountID(accountID)
                    .orElseThrow(() -> new BankAccountNotFoundException(accountID));
            this.accountLocker.lockByAccountID(accountID);
            try {
                if (!bankAccount.mayWithdraw(amount)) {
                    throw new InsufficientFundsException(accountID);
                }
                bankAccount.withdraw(amount);
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
