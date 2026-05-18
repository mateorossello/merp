package dev.mateorossello.merp.modules.accounting.dtos;

import dev.mateorossello.merp.modules.accounting.models.AccountType;

public record AccountOutput(
    Long id,

    Long parentAccountId,

    String code,

    AccountType type,

    String name,

    String description,

    boolean receiveBalance,

    boolean state
) {}
