package dev.mateorossello.merp.modules.sales.dtos;

import java.math.BigDecimal;

public record ItemOutput(
    Long id,

    String code,

    String name,

    String description,

    boolean available,

    BigDecimal unitPrice,

    BigDecimal currentStock,

    BigDecimal minimumStock,

    BigDecimal iva
) {}
