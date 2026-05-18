package dev.mateorossello.merp.modules.accounting.dtos;

import dev.mateorossello.merp.modules.accounting.models.AccountType;
import jakarta.validation.constraints.NotBlank;

public record AccountInput(
    Long parentAccountId,

    @NotBlank(message = "Code is required")
    String code,

    AccountType type,

    @NotBlank(message = "Name is required")
    String name,

    @NotBlank(message = "Description is required")
    String description
) {}
