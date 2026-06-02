package dev.mateorossello.merp.modules.sales.dtos.documents;

import java.math.BigDecimal;

public record DeliveryNoteItemOutput(
    Long itemId,

    BigDecimal quantity
) {}
