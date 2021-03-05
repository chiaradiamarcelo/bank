package gateway;

public interface BankAccountLocker {
    void lockBankAccountByID(Long accountID);

    void unlockBankAccountByID(Long accountID);
}
