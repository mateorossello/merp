package dev.mateorossello.merp.modules.sales.mappers;

import dev.mateorossello.merp.modules.sales.dtos.TransactionItemInput;
import dev.mateorossello.merp.modules.sales.dtos.TransactionItemOutput;
import dev.mateorossello.merp.modules.sales.models.TransactionItem;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionItemMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transaction", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    TransactionItem toEntity(TransactionItemInput transactionItemInput);

    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "itemName", source = "item.name")
    TransactionItemOutput toOutput(TransactionItem transactionItem);
    List<TransactionItemOutput> toOutputList(List<TransactionItem> transactionItems);
}
