package dev.mateorossello.merp.modules.sales.mappers.documents;

import dev.mateorossello.merp.modules.sales.dtos.documents.DeliveryNoteItemInput;
import dev.mateorossello.merp.modules.sales.dtos.documents.DeliveryNoteItemOutput;
import dev.mateorossello.merp.modules.sales.models.documents.DeliveryNoteItem;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeliveryNoteItemMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deliveryNote", ignore = true)
    DeliveryNoteItem toEntity(DeliveryNoteItemInput deliveryNoteItemInput);

    DeliveryNoteItemOutput toOutput(DeliveryNoteItem deliveryNoteItem);
    List<DeliveryNoteItemOutput> toOutputList(List<DeliveryNoteItem> deliveryNoteItems);
}
