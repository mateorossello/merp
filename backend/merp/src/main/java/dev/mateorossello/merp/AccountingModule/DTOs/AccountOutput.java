package dev.mateorossello.merp.AccountingModule.DTOs;

import dev.mateorossello.merp.AccountingModule.Models.AccountType;

public record AccountOutput(
    Long id,

    Long parentAccountId,

    String code,

    AccountType type,

    String name,

    String description,

    Boolean receiveBalance,

    Boolean state
) {}
