package dev.mateorossello.merp.modules.sales.mappers;

import dev.mateorossello.merp.modules.sales.dtos.CustomerInput;
import dev.mateorossello.merp.modules.sales.dtos.CustomerOutput;
import dev.mateorossello.merp.modules.sales.models.Customer;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface CustomerMapper {
    @Mapping(target = "id", ignore = true)
    Customer toEntity(CustomerInput customerInput);

    CustomerOutput toOutput(Customer customer);
    List<CustomerOutput> toOutputList(List<Customer> customers);
}
