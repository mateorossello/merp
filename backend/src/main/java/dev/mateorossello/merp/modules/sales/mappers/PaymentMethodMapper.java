package dev.mateorossello.merp.modules.sales.mappers;

import dev.mateorossello.merp.modules.sales.dtos.PaymentMethodInput;
import dev.mateorossello.merp.modules.sales.dtos.PaymentMethodOutput;
import dev.mateorossello.merp.modules.sales.models.PaymentMethod;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {
    @Mapping(target = "id", ignore = true)
    PaymentMethod toEntity(PaymentMethodInput paymentMethodInput);

    PaymentMethodOutput toOutput(PaymentMethod paymentMethod);
    List<PaymentMethodOutput> toOutputList(List<PaymentMethod> paymentMethods);
}
