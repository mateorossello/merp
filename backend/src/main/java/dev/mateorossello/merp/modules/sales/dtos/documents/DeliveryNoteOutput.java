package dev.mateorossello.merp.modules.sales.dtos.documents;

import dev.mateorossello.merp.modules.sales.dtos.TransactionOutput;
import dev.mateorossello.merp.modules.sales.models.documents.DeliveryNoteType;
import java.time.LocalDate;
import java.util.List;

public record DeliveryNoteOutput(
    Long id,

    Long number,

    LocalDate issueDate,

    TransactionOutput transaction,

    Long createdByUserId,

    LocalDate deliveryDate,

    boolean cancelled,

    String cancellationReason,

    DeliveryNoteType deliveryNoteType,

    List<DeliveryNoteItemOutput> deliveryNoteItemsOutput
) {}
