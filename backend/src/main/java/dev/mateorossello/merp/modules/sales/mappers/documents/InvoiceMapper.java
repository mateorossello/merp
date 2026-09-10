package dev.mateorossello.merp.modules.sales.mappers.documents;

import dev.mateorossello.merp.modules.sales.dtos.documents.InvoiceInput;
import dev.mateorossello.merp.modules.sales.dtos.documents.InvoiceOutput;
import dev.mateorossello.merp.modules.sales.mappers.TransactionMapper;
import dev.mateorossello.merp.modules.sales.models.documents.Invoice;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TransactionMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface InvoiceMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "number", ignore = true)
    @Mapping(target = "transaction", ignore = true)
    @Mapping(target = "createdByUserId", ignore = true)
    @Mapping(target = "invoiceType", ignore = true)
    Invoice toEntity(InvoiceInput invoiceInput);

    InvoiceOutput toOutput(Invoice invoice);
    List<InvoiceOutput> toOutputList(List<Invoice> invoices);
}
