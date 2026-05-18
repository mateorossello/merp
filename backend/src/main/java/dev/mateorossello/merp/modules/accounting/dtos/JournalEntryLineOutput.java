package dev.mateorossello.merp.modules.accounting.dtos;

import java.math.BigDecimal;

public record JournalEntryLineOutput(
    Long id,

    Long accountId,

    String accountName,
    
    BigDecimal amount,

    boolean debit,

    String reference
) {}
