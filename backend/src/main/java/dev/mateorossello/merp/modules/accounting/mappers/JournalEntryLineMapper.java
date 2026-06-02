package dev.mateorossello.merp.modules.accounting.mappers;

import dev.mateorossello.merp.modules.accounting.dtos.JournalEntryLineInput;
import dev.mateorossello.merp.modules.accounting.dtos.JournalEntryLineOutput;
import dev.mateorossello.merp.modules.accounting.models.JournalEntryLine;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JournalEntryLineMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "journalEntry", ignore = true)
    @Mapping(target = "account", ignore = true)
    JournalEntryLine toEntity(JournalEntryLineInput journalEntryLineInput);

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "accountName", source = "account.name")
    JournalEntryLineOutput toOutput(JournalEntryLine journalEntryLine);

    List<JournalEntryLineOutput> toOutputList(List<JournalEntryLine> journalEntryLines);
}
