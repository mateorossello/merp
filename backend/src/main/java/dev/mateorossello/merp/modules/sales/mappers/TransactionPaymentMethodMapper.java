package dev.mateorossello.merp.modules.sales.mappers;

import dev.mateorossello.merp.modules.sales.dtos.TransactionPaymentMethodInput;
import dev.mateorossello.merp.modules.sales.dtos.TransactionPaymentMethodOutput;
import dev.mateorossello.merp.modules.sales.models.TransactionPaymentMethod;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionPaymentMethodMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transaction", ignore = true)
    @Mapping(target = "paymentMethod", ignore = true)
    TransactionPaymentMethod toEntity(TransactionPaymentMethodInput transactionPaymentMethodInput);

    @Mapping(target = "paymentMethodId", source = "paymentMethod.id")
    @Mapping(target = "paymentMethodName", source = "paymentMethod.name")
    TransactionPaymentMethodOutput toOutput(TransactionPaymentMethod transactionPaymentMethod);
    List<TransactionPaymentMethodOutput> toOutputList(List<TransactionPaymentMethod> transactionPaymentMethods);
}
