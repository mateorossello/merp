package dev.mateorossello.merp.modules.sales.dtos.documents;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record NoteItemInput(
    @NotNull(message = "Item ID is required.")
    Long itemId,

    @NotNull(message = "Quantity is required.")
    @Positive(message = "Quantity must be greater than 0.")
    BigDecimal quantity
) {}
