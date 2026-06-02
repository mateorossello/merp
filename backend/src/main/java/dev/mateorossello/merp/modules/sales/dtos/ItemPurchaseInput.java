package dev.mateorossello.merp.modules.sales.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ItemPurchaseInput(
    @NotNull(message = "Item ID is required")
    Long itemId,

    @NotNull(message = "Purchase unit price is required")
    @Positive(message = "Purchase unit price must be greater than 0")
    BigDecimal purchaseUnitPrice,

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    BigDecimal quantity
) {}
