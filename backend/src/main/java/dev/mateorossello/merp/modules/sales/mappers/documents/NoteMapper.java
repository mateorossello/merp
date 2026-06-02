package dev.mateorossello.merp.modules.sales.mappers.documents;

import dev.mateorossello.merp.modules.sales.dtos.documents.NoteInput;
import dev.mateorossello.merp.modules.sales.dtos.documents.NoteOutput;
import dev.mateorossello.merp.modules.sales.mappers.TransactionMapper;
import dev.mateorossello.merp.modules.sales.models.documents.Note;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {InvoiceMapper.class, TransactionMapper.class, NoteItemMapper.class, NoteAdjustmentMapper.class})
public interface NoteMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "number", ignore = true)
    @Mapping(target = "invoice", ignore = true)
    @Mapping(target = "transaction", ignore = true)
    @Mapping(target = "createdByUserId", ignore = true)
    @Mapping(target = "noteItems", ignore = true)
    @Mapping(target = "noteAdjustments", ignore = true)
    Note toEntity(NoteInput noteInput);

    @Mapping(target = "noteItemsOutput", source = "noteItems")
    @Mapping(target = "noteAdjustmentsOutput", source = "noteAdjustments")
    NoteOutput toOutput(Note note);
    List<NoteOutput> toOutputList(List<Note> notes);
}
