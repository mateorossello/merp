package dev.mateorossello.merp.modules.sales.dtos.documents;

import java.math.BigDecimal;

public record NoteAdjustmentOutput(
    String description,

    BigDecimal amount
) {}
