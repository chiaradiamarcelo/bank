package bank.application.usecases;

import java.util.UUID;

public class BankAccountNotFoundException extends RuntimeException {
    public BankAccountNotFoundException(UUID accountID) {
        super("No bank account found with Id: " + accountID);
    }
}
