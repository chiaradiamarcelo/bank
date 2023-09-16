package bank.application;

import java.math.BigDecimal;
import java.util.UUID;

public class TestFixtures {
    public static final UUID BANK_ACCOUNT_ID = UUID.randomUUID();
    public static final UUID NON_EXISTENT_BANK_ACCOUNT_ID = UUID.randomUUID();
    public static final BigDecimal ZERO_EUROS = BigDecimal.ZERO;
    public static final BigDecimal TEN_EUROS = BigDecimal.TEN;
}