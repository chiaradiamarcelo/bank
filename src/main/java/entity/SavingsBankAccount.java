package entity;

import java.math.BigDecimal;

public class SavingsBankAccount extends BankAccount {

    private long interestRate = 0L;

    public SavingsBankAccount(Long accountID, Owner owner, long interestRate) {
        super(accountID, owner);
        this.interestRate = interestRate;
    }

    @Override
    public boolean mayWithdraw(BigDecimal amount) {
        return this.balance.compareTo(amount) >= 0;
    }

    public BigDecimal calculateInterest() {
        // interestRate * balance / 100
        return BigDecimal.valueOf(this.interestRate).multiply(this.balance).divide(BigDecimal.valueOf(100));
    }

    @Override
    public String toString() {
        return "SavingsBankAccount [interestRate=" + interestRate + "]";
    }
}
