package dev.mateorossello.merp.AccountingModule.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;
import java.util.List;
import java.time.LocalDate;

public record JournalEntryInput(
    @NotNull(message = "Entry date is required")
    LocalDate entryDate,

    @NotBlank(message = "Description is required")
    String description,

    @Size(min = 2, message = "At least two journal entry lines are required")
    @Valid
    List<JournalEntryLineInput> journalEntryLinesInput
) {}
