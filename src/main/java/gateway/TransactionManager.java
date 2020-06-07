package gateway;

public interface TransactionManager {

    void beginTransaction();

    void commitTransaction();

    void rollbackTransaction();
}
