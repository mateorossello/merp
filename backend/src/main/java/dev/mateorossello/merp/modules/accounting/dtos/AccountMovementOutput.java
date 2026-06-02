package dev.mateorossello.merp.modules.accounting.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountMovementOutput(
    LocalDate date,

    String description,

    BigDecimal debit,

    BigDecimal credit,
    
    BigDecimal balance
) {}
