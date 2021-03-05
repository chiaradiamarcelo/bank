package usecase.exception;

@SuppressWarnings("serial")
public class InsufficientFundsException extends Exception {
    public InsufficientFundsException(final Long accountID) {
        super("Insufficient funds in bank account with ID " + accountID);
    }
}
