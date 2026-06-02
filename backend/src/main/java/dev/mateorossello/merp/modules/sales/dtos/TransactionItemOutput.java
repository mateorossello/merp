package dev.mateorossello.merp.modules.sales.dtos;

import java.math.BigDecimal;

public record TransactionItemOutput(
    Long itemId,

    String itemName,

    BigDecimal unitPrice,

    BigDecimal unitCost,

    BigDecimal quantity,

    BigDecimal discount,
    
    BigDecimal iva,

    BigDecimal subtotal
) {}
