package bank.application.domain;

import java.util.UUID;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(UUID accountId) {
        super("Insufficient funds in bank account with Id: " + accountId);
    }
}
