package dev.mateorossello.merp.AccountingModule.Repositories;

import dev.mateorossello.merp.AccountingModule.Models.Account;
import dev.mateorossello.merp.AccountingModule.Models.AccountType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface which extends JpaRepository to provide CRUD operations for Account entities.
 */

public interface AccountRepository extends JpaRepository<Account, Long> {
    // Parent account related queries
    boolean existsByParentAccountId(Long parentAccountId);

    // Code related queries
    Optional<Account> findByCode(String code);
    boolean existsByCode(String code);
    @EntityGraph(attributePaths = {"parentAccount"})
    Optional<Account> findFullByCode(String code);

    // Type related queries
    List<Account> findAllByType(AccountType type);

    // Name related queries
    Optional<Account> findByName(String name);
    boolean existsByName(String name);

    // State and receive balance related queries
    List<Account> findAllByReceiveBalance(boolean receiveBalance);
    List<Account> findAllByState(boolean state);
    List<Account> findAllByReceiveBalanceAndState(boolean receiveBalance, boolean state);
}
