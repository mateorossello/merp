package dev.mateorossello.merp.AccountingModule.DTOs;

import java.util.List;

public record GeneralJournalOutput(
    List<JournalEntryOutput> journalEntriesOutput
) {}
