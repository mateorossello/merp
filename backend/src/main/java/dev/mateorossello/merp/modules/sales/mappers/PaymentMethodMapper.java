package dev.mateorossello.merp.modules.sales.mappers;

import dev.mateorossello.merp.modules.sales.dtos.PaymentMethodInput;
import dev.mateorossello.merp.modules.sales.dtos.PaymentMethodOutput;
import dev.mateorossello.merp.modules.sales.models.PaymentMethod;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PaymentMethodMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", ignore = true)
    PaymentMethod toEntity(PaymentMethodInput paymentMethodInput);

    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "account.name", target = "accountName")
    PaymentMethodOutput toOutput(PaymentMethod paymentMethod);
    List<PaymentMethodOutput> toOutputList(List<PaymentMethod> paymentMethods);
}
