package dev.mateorossello.merp.modules.sales.dtos;

import dev.mateorossello.merp.modules.sales.models.FiscalType;

public record CustomerOutput(
    Long id,

    String legalName,

    AddressOutput address,

    String cuit,

    String phone,

    String email,
    
    FiscalType fiscalType
) {}
