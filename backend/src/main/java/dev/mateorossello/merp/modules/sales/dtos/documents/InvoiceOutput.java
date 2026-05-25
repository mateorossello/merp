package dev.mateorossello.merp.modules.sales.dtos.documents;

import dev.mateorossello.merp.modules.sales.dtos.TransactionOutput;
import dev.mateorossello.merp.modules.sales.models.documents.InvoiceType;
import java.time.LocalDate;

public record InvoiceOutput(
    Long id,

    Long number,

    LocalDate issueDate,

    TransactionOutput transaction,

    Long createdByUserId,

    LocalDate dueDate,

    InvoiceType invoiceType
) {}
