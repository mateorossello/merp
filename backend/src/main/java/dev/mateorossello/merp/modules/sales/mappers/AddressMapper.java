package dev.mateorossello.merp.modules.sales.mappers;

import dev.mateorossello.merp.modules.sales.dtos.AddressInput;
import dev.mateorossello.merp.modules.sales.dtos.AddressOutput;
import dev.mateorossello.merp.modules.sales.models.Address;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AddressMapper {
    @Mapping(target = "id", ignore = true)
    Address toEntity(AddressInput addressInput);

    AddressOutput toOutput(Address address);
    List<AddressOutput> toOutputList(List<Address> addresses);
}
