package usecase.exception;

@SuppressWarnings("serial")
public class BankAccountNotFoundException extends Exception{

    public BankAccountNotFoundException(Long accountID) {
        super("No bank account found with ID " + accountID);
    }

}
