package dev.mateorossello.merp.modules.sales.repositories;

import dev.mateorossello.merp.modules.sales.models.TransactionItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface which extends JpaRepository to provide CRUD operations for TransactionItem entities.
 */

public interface TransactionItemRepository extends JpaRepository<TransactionItem, Long> {
    List<TransactionItem> findByItemId(Long itemId);
    boolean existsByItemId(Long itemId);
}
