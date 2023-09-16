package bank.application.usecases.withdraw;

import java.math.BigDecimal;
import java.util.UUID;

public record WithdrawRequest(UUID accountId, BigDecimal amount) {
}