package dev.mateorossello.merp.AccountingModule.Mappers;

import dev.mateorossello.merp.AccountingModule.DTOs.JournalEntryInput;
import dev.mateorossello.merp.AccountingModule.DTOs.JournalEntryOutput;
import dev.mateorossello.merp.AccountingModule.Models.JournalEntry;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {JournalEntryLineMapper.class})
public interface JournalEntryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdByUserId", ignore = true)
    @Mapping(target = "journalEntryLines", source = "journalEntryLinesInput")
    JournalEntry toEntity(JournalEntryInput journalEntryInput);

    @Mapping(target = "journalEntryLinesOutput", source = "journalEntryLines")
    JournalEntryOutput toOutput(JournalEntry journalEntry);

    List<JournalEntryOutput> toOutputList(List<JournalEntry> journalEntries);
}
