package bank.application.domain;

import java.math.BigDecimal;

public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(BigDecimal amount) {
        super("Invalid amount found: " + amount);
    }
}
