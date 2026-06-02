package dev.mateorossello.merp.modules.sales.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import java.util.List;

public record TransactionInput(
    @NotNull(message = "Customer ID is required")
    Long customerId,

    @NotEmpty(message = "Transaction items are required")
    @Valid
    List<TransactionItemInput> transactionItemsInput,

    @NotEmpty(message = "Transaction payment methods are required")
    @Valid
    List<TransactionPaymentMethodInput> transactionPaymentMethodsInput
) {}
