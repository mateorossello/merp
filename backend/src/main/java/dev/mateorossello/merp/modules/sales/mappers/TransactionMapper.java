package dev.mateorossello.merp.modules.sales.mappers;

import dev.mateorossello.merp.modules.sales.dtos.TransactionOutput;
import dev.mateorossello.merp.modules.sales.models.Transaction;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class, TransactionItemMapper.class, TransactionPaymentMethodMapper.class})
public interface TransactionMapper {
    @Mapping(target = "transactionItemsOutput", source = "transactionItems")
    @Mapping(target = "transactionPaymentMethodsOutput", source = "transactionPayments")
    TransactionOutput toOutput(Transaction transaction);
    List<TransactionOutput> toOutputList(List<Transaction> transactions);
}
