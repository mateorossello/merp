package dev.mateorossello.merp.modules.sales.dtos.documents;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record NoteAdjustmentInput(
    @NotBlank(message = "Description is required.")
    String description,

    @NotNull(message = "Amount is required.")
    @Positive(message = "Amount must be greater than 0.")
    BigDecimal amount
) {}
