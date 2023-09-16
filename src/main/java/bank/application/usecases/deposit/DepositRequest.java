package bank.application.usecases.deposit;

import java.math.BigDecimal;
import java.util.UUID;

public record DepositRequest(UUID accountId, BigDecimal amount) {
}