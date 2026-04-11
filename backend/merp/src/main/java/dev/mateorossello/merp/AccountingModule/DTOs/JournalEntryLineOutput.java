package dev.mateorossello.merp.AccountingModule.DTOs;

import java.math.BigDecimal;

public record JournalEntryLineOutput(
    Long id,

    Long accountId,

    String accountName,
    
    BigDecimal amount,

    boolean isDebit,

    String reference
) {}
