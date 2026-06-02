package dev.mateorossello.merp.modules.sales.repositories;

import dev.mateorossello.merp.modules.sales.models.Item;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Interface which extends JpaRepository to provide CRUD operations for Item entities.
 */

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByCode(String code);
    boolean existsByCode(String code);
    List<Item> findAllByAvailable(boolean available);

    @Query("SELECT item FROM Item item WHERE item.currentStock < item.minimumStock")
    List<Item> findAllByCurrentStockLessThanMinimumStock();
}
