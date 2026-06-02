package dev.mateorossello.merp.modules.sales.dtos;

public record AddressOutput(
    Long id,

    String country,

    String province,

    String city,

    String postalCode,

    String street,

    String streetNumber
) {}
