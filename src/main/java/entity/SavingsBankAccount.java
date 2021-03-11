package entity;

import java.math.BigDecimal;

public class SavingsBankAccount extends BankAccount {
    private long interestRate = 0L;

    public SavingsBankAccount(final Long accountID, final Owner owner, final long interestRate) {
        super(accountID, owner);
        this.interestRate = interestRate;
    }

    @Override
    public boolean mayWithdraw(final BigDecimal amount) {
        return this.getBalance().compareTo(amount) >= 0;
    }

    public BigDecimal calculateInterest() {
        // interestRate * balance / 100
        return BigDecimal.valueOf(this.interestRate).multiply(this.getBalance()).divide(BigDecimal.valueOf(100));
    }

    @Override
    public String toString() {
        return "SavingsBankAccount [interestRate=" + this.interestRate + "]";
    }
}
