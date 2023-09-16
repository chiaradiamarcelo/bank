package bank.repository;

import bank.application.contract.BankAccountRepositoryTest;
import bank.application.usecases.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@Import(value = JpaBankAccountRepository.class)
@TestPropertySource(locations = { "classpath:application-test.properties" })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JPABankAccountRepositoryIT extends BankAccountRepositoryTest {
    @Autowired
    private JpaBankAccountRepository bankAccountRepository;
    @Autowired
    private CrudBankAccountRepository crudBankAccountRepository;

    @BeforeEach
    void setUp() {
        crudBankAccountRepository.deleteAll();
    }

    @Override
    protected BankAccountRepository repository() {
        return bankAccountRepository;
    }
}
