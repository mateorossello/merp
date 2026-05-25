package dev.mateorossello.merp.modules.sales.dtos;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record TransactionItemInput(
    @NotNull
    Long itemId,

    @NotNull
    @PositiveOrZero(message = "Unit price must be greater than or equal to 0")
    BigDecimal unitPrice,

    @NotNull
    @PositiveOrZero(message = "Unit cost must be greater than or equal to 0")
    BigDecimal unitCost,

    @NotNull
    @Positive(message = "Quantity must be greater than 0")
    BigDecimal quantity,

    @NotNull
    @DecimalMin(value = "0.0", message = "Discount must be greater than or equal to 0")
    @DecimalMax(value = "100.0", message = "Discount must be less than or equal to 100")
    BigDecimal discount,

    @NotNull
    @DecimalMin(value = "0.0", message = "IVA must be greater than or equal to 0")
    @DecimalMax(value = "100.0", message = "IVA must be less than or equal to 100")
    BigDecimal iva
) {}
