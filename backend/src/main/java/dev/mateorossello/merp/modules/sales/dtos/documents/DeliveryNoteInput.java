package dev.mateorossello.merp.modules.sales.dtos.documents;

import dev.mateorossello.merp.modules.sales.models.documents.DeliveryNoteType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

public record DeliveryNoteInput(
    @NotNull(message = "Issue date is required")
    LocalDate issueDate,

    Long transactionId,

    @NotNull(message = "Delivery date is required")
    LocalDate deliveryDate,

    @NotNull(message = "Delivery note type is required")
    DeliveryNoteType deliveryNoteType,

    @NotEmpty(message = "Delivery items are required")
    @Valid
    List<DeliveryNoteItemInput> deliveryNoteItemsInput
) {}
