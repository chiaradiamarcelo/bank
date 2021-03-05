package usecase.payinterest;

import entity.SavingsBankAccount;
import gateway.*;
import usecase.exception.BankAccountNotFoundException;

public class PayInterestInteractor implements PayInterestUseCase {
    private final BankAccountLocker accountLocker;
    private final BankAccountRepository<SavingsBankAccount> bankAccountRepository;
    private final TransactionManager transactionManager;

    public PayInterestInteractor(final BankAccountLocker accountLocker,
            final BankAccountRepository<SavingsBankAccount> bankAccountRepository,
            final TransactionManager transactionManager) {
        this.accountLocker = accountLocker;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    public void payInterest(final Long accountID) throws BankAccountNotFoundException {
        this.beginTransaction();
        try {
            this.payInterestTo(accountID);
        } catch (final Exception e) {
            this.rollbackTransaction();
            throw e;
        }
    }

    private void beginTransaction() {
        this.transactionManager.beginTransaction();
    }

    private void payInterestTo(final Long accountID) throws BankAccountNotFoundException {
        final SavingsBankAccount bankAccount = this.getBankAccount(accountID);
        this.lockBankAccount(accountID);
        try {
            bankAccount.deposit(bankAccount.calculateInterest());
            this.bankAccountRepository.save(bankAccount);
            this.transactionManager.commitTransaction();
        } finally {
            this.unlockBankAccount(accountID);
        }
    }

    private SavingsBankAccount getBankAccount(final Long accountID) throws BankAccountNotFoundException {
        return this.bankAccountRepository.getByAccountID(accountID)
                .orElseThrow(() -> new BankAccountNotFoundException(accountID));
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
