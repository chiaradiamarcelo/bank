package usecase.deposit;

import java.math.BigDecimal;

import entity.BankAccount;
import gateway.*;
import usecase.exception.BankAccountNotFoundException;

public class DepositInteractor implements DepositUseCase {
    private final BankAccountLocker accountLocker;
    private final BankAccountRepository<BankAccount> bankAccountRepository;
    private final TransactionManager transactionManager;

    public DepositInteractor(final BankAccountLocker accountLocker,
            final BankAccountRepository<BankAccount> bankAccountRepository,
            final TransactionManager transactionManager) {
        this.accountLocker = accountLocker;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    public void deposit(final Long accountID, final BigDecimal amount) throws BankAccountNotFoundException {
        final BankAccount bankAccount = this.getBankAccount(accountID);
        this.lockBankAccount(accountID);
        try {
            this.executeOnTransaction(() -> this.depositTo_(bankAccount, amount));
        } finally {
            this.unlockBankAccount(accountID);
        }
    }

    private void executeOnTransaction(final TransactionalTest transactional) {
        this.beginTransaction();
        try {
            transactional.execute();
            this.commitTransaction();
        } catch (final Exception e) {
            this.rollbackTransaction();
            throw e;
        }
    }

    private void depositTo_(final BankAccount bankAccount, final BigDecimal amount) {
        bankAccount.deposit(amount);
        this.bankAccountRepository.save(bankAccount);
    }

    private void commitTransaction() {
        this.transactionManager.commitTransaction();
    }

    private void beginTransaction() {
        this.transactionManager.beginTransaction();
    }

    private BankAccount getBankAccount(final Long accountID) throws BankAccountNotFoundException {
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
