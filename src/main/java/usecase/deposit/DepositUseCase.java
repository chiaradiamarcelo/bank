package usecase.deposit;

import java.math.BigDecimal;

import usecase.exception.BankAccountNotFoundException;

public interface DepositUseCase {
    void deposit(Long accountID, BigDecimal amount) throws BankAccountNotFoundException;
}
