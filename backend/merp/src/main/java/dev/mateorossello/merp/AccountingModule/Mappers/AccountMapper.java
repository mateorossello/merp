package dev.mateorossello.merp.AccountingModule.Mappers;

import dev.mateorossello.merp.AccountingModule.DTOs.AccountInput;
import dev.mateorossello.merp.AccountingModule.DTOs.AccountOutput;
import dev.mateorossello.merp.AccountingModule.Models.Account;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
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
