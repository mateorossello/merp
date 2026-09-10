package dev.mateorossello.merp.modules.sales.dtos;

public record PaymentMethodOutput(
    Long id,

    String name,

    Long accountId,

    String accountName
) {}
