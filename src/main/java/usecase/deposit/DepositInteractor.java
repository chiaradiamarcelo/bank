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
        this.beginTransaction();
        try {
            this.depositTo(accountID, amount);
        } catch (final Exception e) {
            this.rollbackTransaction();
            throw e;
        }
    }

    private void beginTransaction() {
        this.transactionManager.beginTransaction();
    }

    private void depositTo(final Long accountID, final BigDecimal amount) throws BankAccountNotFoundException {
        final BankAccount bankAccount = this.getBankAccount(accountID);
        this.lockBankAccount(accountID);
        try {
            bankAccount.deposit(amount);
            this.bankAccountRepository.save(bankAccount);
            this.transactionManager.commitTransaction();
        } finally {
            this.unlockBankAccount(accountID);
        }
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
