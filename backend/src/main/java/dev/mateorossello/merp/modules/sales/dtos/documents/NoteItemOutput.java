package dev.mateorossello.merp.modules.sales.dtos.documents;

import java.math.BigDecimal;

public record NoteItemOutput(
    Long itemId,

    BigDecimal quantity
) {}
