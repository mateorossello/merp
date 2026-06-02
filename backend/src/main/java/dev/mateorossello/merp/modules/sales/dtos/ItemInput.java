package dev.mateorossello.merp.modules.sales.dtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ItemInput(
    @NotBlank(message = "Name cannot be blank")
    String name,

    String description,

    @NotNull
    Boolean available,

    @NotNull
    @PositiveOrZero(message = "Minimum stock must be greater than or equal to 0")
    BigDecimal minimumStock,

    @NotNull
    @DecimalMin(value = "0.0", message = "IVA must be greater than or equal to 0")
    @DecimalMax(value = "100.0", message = "IVA must be less than or equal to 100")
    BigDecimal iva
) {}
