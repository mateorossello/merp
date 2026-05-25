package dev.mateorossello.merp.modules.sales.dtos;

import java.math.BigDecimal;

public record TransactionPaymentMethodOutput(
    Long paymentMethodId,

    String paymentMethodName,

    BigDecimal amount
) {}
