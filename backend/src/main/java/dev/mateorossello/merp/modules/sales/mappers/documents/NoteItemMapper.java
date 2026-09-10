package dev.mateorossello.merp.modules.sales.mappers.documents;

import dev.mateorossello.merp.modules.sales.dtos.documents.NoteItemInput;
import dev.mateorossello.merp.modules.sales.dtos.documents.NoteItemOutput;
import dev.mateorossello.merp.modules.sales.models.documents.NoteItem;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface NoteItemMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "note", ignore = true)
    @Mapping(target = "item", ignore = true)
    NoteItem toEntity(NoteItemInput noteItemInput);
    List<NoteItem> toEntityList(List<NoteItemInput> noteItemInputs);
    
    @Mapping(target = "itemId", source = "item.id")
    NoteItemOutput toOutput(NoteItem noteItem);
    List<NoteItemOutput> toOutputList(List<NoteItem> noteItems);
}
