package dev.mateorossello.merp.modules.accounting.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record JournalEntryOutput(
    Long id,

    LocalDate entryDate,

    LocalDateTime createdAt,

    String description,

    Long createdByUserId,

    List<JournalEntryLineOutput> journalEntryLinesOutput
) {}
