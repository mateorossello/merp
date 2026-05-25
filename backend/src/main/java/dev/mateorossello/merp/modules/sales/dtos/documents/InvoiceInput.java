package dev.mateorossello.merp.modules.sales.dtos.documents;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record InvoiceInput(
    @NotNull(message = "Issue date is required")
    LocalDate issueDate,
    
    @NotNull(message = "Transaction ID is required")
    Long transactionId,

    @NotNull(message = "Due date is required")
    LocalDate dueDate
) {}
