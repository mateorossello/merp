package dev.mateorossello.merp.modules.sales.mappers.documents;

import dev.mateorossello.merp.modules.sales.dtos.documents.DeliveryNoteInput;
import dev.mateorossello.merp.modules.sales.dtos.documents.DeliveryNoteOutput;
import dev.mateorossello.merp.modules.sales.mappers.TransactionMapper;
import dev.mateorossello.merp.modules.sales.models.documents.DeliveryNote;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TransactionMapper.class, DeliveryNoteItemMapper.class})
public interface DeliveryNoteMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "number", ignore = true)
    @Mapping(target = "transaction", ignore = true)
    @Mapping(target = "createdByUserId", ignore = true)
    @Mapping(target = "deliveryNoteItems", ignore = true)
    @Mapping(target = "cancelled", ignore = true)
    @Mapping(target = "cancellationReason", ignore = true)
    DeliveryNote toEntity(DeliveryNoteInput deliveryNoteInput);

    @Mapping(target = "deliveryNoteItemsOutput", source = "deliveryNoteItems")
    DeliveryNoteOutput toOutput(DeliveryNote deliveryNote);
    List<DeliveryNoteOutput> toOutputList(List<DeliveryNote> deliveryNotes);
}
