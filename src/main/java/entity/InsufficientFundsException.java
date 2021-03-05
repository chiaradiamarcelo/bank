package entity;

@SuppressWarnings("serial")
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(final Long accountID) {
        super("Insufficient funds in bank account with ID " + accountID);
    }
}
