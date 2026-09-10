package dev.mateorossello.merp.modules.sales.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddressInput(
    @NotBlank(message = "Country is required.")
    String country,

    @NotBlank(message = "Province is required.")
    String province,

    @NotBlank(message = "City is required.")
    String city,

    @NotBlank(message = "Postal code is required.")
    @Pattern(regexp = "^[0-9]*$", message = "Postal code must contain only digits.")
    String postalCode,

    @NotBlank(message = "Street is required.")
    String street,

    @NotBlank(message = "Street number is required.")
    @Pattern(regexp = "^[0-9]*$", message = "Street number must contain only digits.")
    String streetNumber
) {}
