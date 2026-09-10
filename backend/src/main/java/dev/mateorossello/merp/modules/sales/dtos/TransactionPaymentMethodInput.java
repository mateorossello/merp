package dev.mateorossello.merp.modules.sales.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record TransactionPaymentMethodInput(
    @NotNull(message = "Payment method ID is required.")
    Long paymentMethodId,

    @NotNull(message = "Amount is required.")
    @Positive(message = "Amount must be greater than 0.")
    BigDecimal amount
) {}
