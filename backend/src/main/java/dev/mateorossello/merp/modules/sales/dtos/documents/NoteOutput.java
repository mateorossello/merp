package dev.mateorossello.merp.modules.sales.dtos.documents;

import dev.mateorossello.merp.modules.sales.dtos.TransactionOutput;
import dev.mateorossello.merp.modules.sales.models.documents.NoteType;
import java.time.LocalDate;
import java.util.List;

public record NoteOutput(
    Long id,

    Long number,

    LocalDate issueDate,

    TransactionOutput transaction,

    Long createdByUserId,

    InvoiceOutput invoice,

    NoteType noteType,

    String reason,

    List<NoteItemOutput> noteItemsOutput,

    List<NoteAdjustmentOutput> noteAdjustmentsOutput
) {}
