package dev.mateorossello.merp.AccountingModule.Repositories;

import dev.mateorossello.merp.AccountingModule.Models.Account;
import dev.mateorossello.merp.AccountingModule.Models.AccountType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByParentAccountId(Long parentAccountId);
    Optional<Account> findByCode(String code);
    boolean existsByCode(String code);
    @EntityGraph(attributePaths = {"parentAccount"})
    Optional<Account> findFullByCode(String code);
    List<Account> findAllByType(AccountType type);
    Optional<Account> findByName(String name);
    boolean existsByName(String name);
    List<Account> findAllByReceiveBalance(Boolean receiveBalance);
    List<Account> findAllByState(Boolean state);
    List<Account> findAllByReceiveBalanceAndState(Boolean receiveBalance, Boolean state);
}
