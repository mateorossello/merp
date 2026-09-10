package dev.mateorossello.merp.modules.sales.mappers;

import dev.mateorossello.merp.modules.sales.dtos.ItemInput;
import dev.mateorossello.merp.modules.sales.dtos.ItemOutput;
import dev.mateorossello.merp.modules.sales.models.Item;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "unitPrice", ignore = true)
    @Mapping(target = "currentStock", ignore = true)
    Item toEntity(ItemInput itemInput);

    ItemOutput toOutput(Item item);
    List<ItemOutput> toOutputList(List<Item> items);
}
