package dev.mateorossello.merp.AccountingModule.DTOs;

import dev.mateorossello.merp.AccountingModule.Models.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AccountInput(
    Long parentAccountId,

    @NotNull(message = "Code is required")
    String code,

    AccountType type,

    @NotBlank(message = "Name is required")
    String name,

    @NotBlank(message = "Description is required")
    String description
) {}
