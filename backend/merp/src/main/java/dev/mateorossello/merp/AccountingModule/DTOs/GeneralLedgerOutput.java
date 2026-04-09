package dev.mateorossello.merp.AccountingModule.DTOs;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record GeneralLedgerOutput(
    Long accountId,

    String accountName,

    LocalDate startDate,

    LocalDate endDate,

    BigDecimal initialBalance,

    List<AccountMovementOutput> accountMovementsOutput
) {}
