package usecase.transfer;

import java.math.BigDecimal;

import entity.CheckingBankAccount;
import gateway.AccountLocker;
import gateway.BankAccountRepository;
import gateway.TransactionManager;
import usecase.exception.BankAccountNotFoundException;
import usecase.exception.InsufficientFundsException;

public class TransferInteractor implements TransferUseCase {

    private AccountLocker accountLocker;
    private BankAccountRepository<CheckingBankAccount> bankAccountRepository;
    private TransactionManager transactionManager;

    public TransferInteractor(AccountLocker accountLocker,
            BankAccountRepository<CheckingBankAccount> bankAccountRepository, TransactionManager transactionManager) {
        super();
        this.accountLocker = accountLocker;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    public void transfer(Long originAccountID, Long destinationAccountID, BigDecimal amount)
        throws BankAccountNotFoundException, InsufficientFundsException {
        this.transactionManager.beginTransaction();
        try {
            CheckingBankAccount originBankAccount = this.bankAccountRepository.getByAccountID(originAccountID)
                    .orElseThrow(() -> new BankAccountNotFoundException(originAccountID));
            CheckingBankAccount destinationBankAccount = this.bankAccountRepository.getByAccountID(destinationAccountID)
                    .orElseThrow(() -> new BankAccountNotFoundException(destinationAccountID));

            this.accountLocker.lockByAccountID(Math.min(originAccountID, destinationAccountID));
            this.accountLocker.lockByAccountID(Math.max(originAccountID, destinationAccountID));
            try {
                if (!originBankAccount.mayWithdraw(amount)) {
                    throw new InsufficientFundsException(originAccountID);
                }
                originBankAccount.withdraw(amount);
                destinationBankAccount.deposit(amount);
                this.bankAccountRepository.save(originBankAccount);
                this.bankAccountRepository.save(destinationBankAccount);
                this.transactionManager.commitTransaction();
            }
            finally {
                this.accountLocker.unlockByAccountID(Math.max(originAccountID, destinationAccountID));
                this.accountLocker.unlockByAccountID(Math.min(originAccountID, destinationAccountID));
            }
        }
        catch (Exception e) {
            this.transactionManager.rollbackTransaction();
            throw e;
        }
    }

}
