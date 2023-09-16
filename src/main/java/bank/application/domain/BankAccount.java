package bank.application.domain;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class BankAccount {
    private final UUID id;
    private BigDecimal balance;

    public BankAccount(UUID id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }

    public void deposit(BigDecimal amount) {
        if (isNegativeAmount(amount)) {
            throw new InvalidAmountException(amount);
        }
        balance = balance.add(amount);
    }

    private boolean isNegativeAmount(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public void withdraw(BigDecimal amount) {
        if (!mayWithdraw(amount)) {
            throw new InsufficientFundsException(id);
        }
        balance = balance.subtract(amount);
    }

    private boolean mayWithdraw(BigDecimal amount) {
        return balance.compareTo(amount) >= 0;
    }

    public UUID id() {
        return id;
    }

    public BigDecimal balance() {
        return balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BankAccount that = (BankAccount) o;
        return Objects.equals(id, that.id) && Objects.equals(balance, that.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, balance);
    }
}
