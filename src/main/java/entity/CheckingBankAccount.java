package entity;

import java.math.BigDecimal;

public class CheckingBankAccount extends BankAccount {
    private BigDecimal balanceOverdraftLimit = BigDecimal.ZERO;

    public CheckingBankAccount(final Long accountID, final Owner owner, final BigDecimal overdraftLimit) {
        super(accountID, owner);
        this.balanceOverdraftLimit = overdraftLimit;
    }

    @Override
    public boolean mayWithdraw(final BigDecimal amount) {
        return this.getBalance().subtract(amount).add(this.balanceOverdraftLimit).compareTo(BigDecimal.ZERO) >= 0;
    }

    @Override
    public String toString() {
        return "CheckingBankAccount [balanceOverdraftLimit=" + this.balanceOverdraftLimit + "]";
    }
}
