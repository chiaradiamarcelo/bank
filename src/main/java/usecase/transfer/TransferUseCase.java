package usecase.transfer;

import java.math.BigDecimal;

import usecase.exception.BankAccountNotFoundException;
import usecase.exception.InsufficientFundsException;

public interface TransferUseCase {
    void transfer(Long originAccountID, Long destinationAccountID, BigDecimal amount)
            throws BankAccountNotFoundException, InsufficientFundsException;
}