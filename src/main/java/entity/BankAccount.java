package entity;

import java.math.BigDecimal;

public abstract class BankAccount {

    private Long accountID;
    private Owner owner;
    protected BigDecimal balance = BigDecimal.ZERO;

    public BankAccount(Long accountID, Owner owner) {
        super();
        this.accountID = accountID;
        this.owner = owner;
        this.balance = BigDecimal.ZERO;
    }

    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (!this.mayWithdraw(amount)) {
            throw new InsufficientFundsException(this.accountID);
        }
        this.balance = this.balance.subtract(amount);
    }

    public abstract boolean mayWithdraw(BigDecimal amount);

    public BigDecimal getBalance() {
        return this.balance;
    }

    public Long getAccountID() {
        return accountID;
    }

    public Owner getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return "BankAccount [accountID=" + accountID + ", owner=" + owner + ", balance=" + balance + "]";
    }
}
