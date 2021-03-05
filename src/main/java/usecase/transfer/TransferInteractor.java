package usecase.transfer;

import java.math.BigDecimal;

import entity.CheckingBankAccount;
import gateway.*;
import usecase.exception.BankAccountNotFoundException;
import usecase.exception.InsufficientFundsException;

public class TransferInteractor implements TransferUseCase {
    private final BankAccountLocker accountLocker;
    private final BankAccountRepository<CheckingBankAccount> bankAccountRepository;
    private final TransactionManager transactionManager;

    public TransferInteractor(final BankAccountLocker accountLocker,
            final BankAccountRepository<CheckingBankAccount> bankAccountRepository,
            final TransactionManager transactionManager) {
        this.accountLocker = accountLocker;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    public void transfer(final Long originAccountID, final Long destinationAccountID, final BigDecimal amount)
            throws BankAccountNotFoundException, InsufficientFundsException {
        this.beginTransaction();
        try {
            this.transferBetween(originAccountID, destinationAccountID, amount);
        } catch (final Exception e) {
            this.rollbackTransaction();
            throw e;
        }
    }

    private void beginTransaction() {
        this.transactionManager.beginTransaction();
    }

    private void transferBetween(final Long originAccountID, final Long destinationAccountID, final BigDecimal amount)
            throws BankAccountNotFoundException, InsufficientFundsException {
        final CheckingBankAccount originBankAccount = getBankAccount(originAccountID);
        final CheckingBankAccount destinationBankAccount = getBankAccount(destinationAccountID);

        this.lockBankAccount(Math.min(originAccountID, destinationAccountID));
        this.lockBankAccount(Math.max(originAccountID, destinationAccountID));
        try {
            if (!originBankAccount.mayWithdraw(amount)) {
                throw new InsufficientFundsException(originAccountID);
            }
            originBankAccount.withdraw(amount);
            destinationBankAccount.deposit(amount);
            this.bankAccountRepository.save(originBankAccount);
            this.bankAccountRepository.save(destinationBankAccount);
            this.transactionManager.commitTransaction();
        } finally {
            this.unlockBankAccount(Math.max(originAccountID, destinationAccountID));
            this.unlockBankAccount(Math.min(originAccountID, destinationAccountID));
        }
    }

    private CheckingBankAccount getBankAccount(final Long originAccountID) throws BankAccountNotFoundException {
        return this.bankAccountRepository.getByAccountID(originAccountID)
                .orElseThrow(() -> new BankAccountNotFoundException(originAccountID));
    }

    private void lockBankAccount(final Long accountID) {
        this.accountLocker.lockBankAccountByID(accountID);
    }

    private void unlockBankAccount(final Long accountID) {
        this.accountLocker.unlockBankAccountByID(accountID);
    }

    private void rollbackTransaction() {
        this.transactionManager.rollbackTransaction();
    }
}
