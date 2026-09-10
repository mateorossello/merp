package dev.mateorossello.merp.modules.sales.dtos;

import jakarta.validation.constraints.NotBlank;

public record PaymentMethodInput(
    @NotBlank(message = "Name cannot be blank.")
    String name
) {}
