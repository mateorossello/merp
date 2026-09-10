package dev.mateorossello.merp.modules.accounting.mappers;

import dev.mateorossello.merp.modules.accounting.dtos.AccountInput;
import dev.mateorossello.merp.modules.accounting.dtos.AccountOutput;
import dev.mateorossello.merp.modules.accounting.models.Account;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AccountMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentAccount", ignore = true)
    @Mapping(target = "receiveBalance", ignore = true)
    @Mapping(target = "state", ignore = true)
    Account toEntity(AccountInput accountInput);

    @Mapping(target = "parentAccountId", source = "parentAccount.id")
    AccountOutput toOutput(Account account);
    List<AccountOutput> toOutputList(List<Account> accounts);
}
