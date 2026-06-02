package dev.mateorossello.merp.modules.sales.dtos;

import dev.mateorossello.merp.modules.sales.models.FiscalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.Valid;

public record FiscalConfigurationInput(
    @NotBlank(message = "Legal name is required")
    String legalName,

    @NotNull(message = "Address is required")
    @Valid
    AddressInput address,

    @NotBlank(message = "CUIT is required")
    @Pattern(regexp = "^[0-9]{11}$", message = "CUIT must contain only 11 digits")
    String cuit,

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[0-9]*$", message = "Phone must contain only digits")
    String phone,

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email,

    @NotNull(message = "Fiscal type is required")
    FiscalType fiscalType
) {}
