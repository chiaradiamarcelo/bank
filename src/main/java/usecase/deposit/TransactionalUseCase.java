package usecase.deposit;

import java.math.BigDecimal;

import gateway.TransactionManager;

public abstract class TransactionalUseCase {
    private final TransactionManager transactionManager;

    protected TransactionalUseCase(final TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    // use closures! Maybe I can pass the whole block of executable code, as in Smalltalk
    public void executeOnTransaction(final Long accountID, final BigDecimal amount) {
        this.beginTransaction();
        try {
            this.executeUseCase(accountID, amount);
            this.commitTransaction();
        } catch (final Exception e) {
            this.rollbackTransaction();
            throw e;
        }
    }

    private void commitTransaction() {
        this.transactionManager.commitTransaction();
    }

    private void beginTransaction() {
        this.transactionManager.beginTransaction();
    }

    protected abstract void executeUseCase(Long accountID, BigDecimal amount);

    private void rollbackTransaction() {
        this.transactionManager.rollbackTransaction();
    }
}
