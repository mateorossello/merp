package dev.mateorossello.merp.AccountingModule.DTOs;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record JournalEntryLineInput(
    @NotNull(message = "Account ID is required")
    Long accountId,

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    BigDecimal amount,

    @NotNull(message = "Must specify if it is a debit or credit")
    Boolean isDebit,

    String reference
) {}
