package dev.mateorossello.merp.modules.sales.dtos;

import dev.mateorossello.merp.modules.sales.models.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record TransactionOutput(
    Long id,

    TransactionType transactionType,

    LocalDate issueDate,

    Long createdByUserId,

    CustomerOutput customer,

    Long receiptNumber,

    boolean cancelled,

    BigDecimal total,

    List<TransactionItemOutput> transactionItemsOutput,

    List<TransactionPaymentMethodOutput> transactionPaymentMethodsOutput
) {}
