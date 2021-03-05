package usecase.withdraw;

import java.math.BigDecimal;

import usecase.exception.BankAccountNotFoundException;
import usecase.exception.InsufficientFundsException;

public interface WithdrawUseCase {
    void withdraw(Long accountID, BigDecimal amount) throws BankAccountNotFoundException, InsufficientFundsException;
}
