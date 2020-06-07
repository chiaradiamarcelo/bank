package usecase.payinterest;

import entity.SavingsBankAccount;
import gateway.AccountLocker;
import gateway.BankAccountRepository;
import gateway.TransactionManager;
import usecase.exception.BankAccountNotFoundException;

public class PayInterestInteractor implements PayInterestUseCase {

    private AccountLocker accountLocker;
    private BankAccountRepository<SavingsBankAccount> bankAccountRepository;
    private TransactionManager transactionManager;


    public PayInterestInteractor(AccountLocker accountLocker,
            BankAccountRepository<SavingsBankAccount> bankAccountRepository, TransactionManager transactionManager) {
        super();
        this.accountLocker = accountLocker;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    public void payInterest(Long accountID) throws BankAccountNotFoundException {
        this.transactionManager.beginTransaction();
        try {
            SavingsBankAccount bankAccount = this.bankAccountRepository.getByAccountID(accountID)
                    .orElseThrow(() -> new BankAccountNotFoundException(accountID));
            this.accountLocker.lockByAccountID(accountID);
            try {
                bankAccount.deposit(bankAccount.calculateInterest());
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
