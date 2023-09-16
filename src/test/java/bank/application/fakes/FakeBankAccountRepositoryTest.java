package bank.application.fakes;

import bank.application.contract.BankAccountRepositoryTest;
import bank.application.usecases.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;

class FakeBankAccountRepositoryTest extends BankAccountRepositoryTest {
    private FakeBankAccountRepository bankAccountRepository;

    @BeforeEach
    void setUp() {
        this.bankAccountRepository = new FakeBankAccountRepository();
    }

    @Override
    protected BankAccountRepository repository() {
        return bankAccountRepository;
    }
}
