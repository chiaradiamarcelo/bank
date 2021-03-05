package usecase.payinterest;

import usecase.exception.BankAccountNotFoundException;

public interface PayInterestUseCase {
    void payInterest(Long accountID) throws BankAccountNotFoundException;
}
