package dev.mateorossello.merp.modules.accounting.dtos;

import java.util.List;

public record GeneralJournalOutput(
    List<JournalEntryOutput> journalEntriesOutput
) {}
