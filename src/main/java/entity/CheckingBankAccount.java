package entity;

import java.math.BigDecimal;

public class CheckingBankAccount extends BankAccount {

    private BigDecimal balanceOverdraftLimit = BigDecimal.ZERO;

    public CheckingBankAccount(Long accountID, Owner owner, BigDecimal overdraftLimit) {
        super(accountID, owner);
        this.balanceOverdraftLimit = overdraftLimit;
    }

    @Override
    public boolean mayWithdraw(BigDecimal amount) {
        return this.balance.subtract(amount).add(this.balanceOverdraftLimit).compareTo(BigDecimal.ZERO) >= 0;
    }

    @Override
    public String toString() {
        return "CheckingBankAccount [balanceOverdraftLimit=" + balanceOverdraftLimit + "]";
    }

}
