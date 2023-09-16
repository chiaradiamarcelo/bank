package bank.repository;

import bank.application.domain.BankAccount;
import bank.application.usecases.BankAccountRepository;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaBankAccountRepository implements BankAccountRepository {
    private final CrudBankAccountRepository crudBankAccountRepository;

    JpaBankAccountRepository(CrudBankAccountRepository crudBankAccountRepository) {
        this.crudBankAccountRepository = crudBankAccountRepository;
    }

    @Override
    public Optional<BankAccount> accountById(UUID accountId) {
        return crudBankAccountRepository.findById(accountId).map(BankAccountEntity::toBankAccount);
    }

    @Override
    public void save(BankAccount bankAccount) {
        crudBankAccountRepository.save(new BankAccountEntity(bankAccount));
    }

    @Override
    public void updateBalance(UUID accountId, BigDecimal balance) {
        crudBankAccountRepository.updateBalance(accountId, balance);
    }
}

@Repository
interface CrudBankAccountRepository extends JpaRepository<BankAccountEntity, UUID> {
    @Transactional
    @Modifying
    @Query("UPDATE bank_account SET balance = :balance WHERE id = :id")
    void updateBalance(@Param("id") UUID id, @Param("balance") BigDecimal balance);
}

@Entity(name = "bank_account")
class BankAccountEntity {
    @Id
    @Column(name = "id")
    private UUID id;
    @Column(name = "balance")
    private BigDecimal balance;

    public BankAccountEntity() {
    }

    BankAccountEntity(BankAccount bankAccount) {
        this.id = bankAccount.id();
        this.balance = bankAccount.balance();
    }

    BankAccount toBankAccount() {
        return new BankAccount(this.id, this.balance);
    }
}
