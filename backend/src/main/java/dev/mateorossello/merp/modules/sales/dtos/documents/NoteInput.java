package dev.mateorossello.merp.modules.sales.dtos.documents;

import dev.mateorossello.merp.modules.sales.models.documents.NoteType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

public record NoteInput(
    @NotNull(message = "Issue date is required")
    LocalDate issueDate,

    @NotNull(message = "Transaction ID is required")
    Long transactionId,

    @NotNull(message = "Invoice ID is required")
    Long invoiceId,

    @NotNull(message = "Note type is required")
    NoteType noteType,

    @NotBlank(message = "Reason is required")
    String reason,

    @Valid
    List<NoteItemInput> noteItemsInput,

    @Valid
    List<NoteAdjustmentInput> noteAdjustmentsInput
) {}
