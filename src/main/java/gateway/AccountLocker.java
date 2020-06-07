package gateway;

public interface AccountLocker {

    void lockByAccountID(Long accountID);

    void unlockByAccountID(Long accountID);

}
