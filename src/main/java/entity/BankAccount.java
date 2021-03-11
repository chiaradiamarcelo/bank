package entity;

import java.math.BigDecimal;

public abstract class BankAccount {
    private final Long accountID;
    private final Owner owner;
    private BigDecimal balance = BigDecimal.ZERO;

    public BankAccount(final Long accountID, final Owner owner) {
        this.accountID = accountID;
        this.owner = owner;
        this.balance = BigDecimal.ZERO;
    }

    public void deposit(final BigDecimal amount) {
        if (this.isNegativeAmount(amount)) {
            throw new IllegalArgumentException("Attempt to deposit a negative amount in bank account occurred");
        }
        this.balance = this.balance.add(amount);
    }

    private boolean isNegativeAmount(final BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public void withdraw(final BigDecimal amount) {
        if (!this.mayWithdraw(amount)) {
            throw new InsufficientFundsException(this.accountID);
        }
        this.balance = this.balance.subtract(amount);
    }

    public abstract boolean mayWithdraw(BigDecimal amount);

    public BigDecimal getBalance() {
        return this.balance;
    }

    @Override
    public String toString() {
        return "BankAccount [accountID=" + this.accountID + ", owner=" + this.owner + ", balance=" + this.balance + "]";
    }
}
