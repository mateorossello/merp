package dev.mateorossello.merp.modules.sales.dtos;

import dev.mateorossello.merp.modules.sales.models.FiscalType;

public record FiscalConfigurationOutput(
    Long id,

    String legalName,

    AddressOutput address,

    String cuit,

    String phone,

    String email,
    
    FiscalType fiscalType
) {}
