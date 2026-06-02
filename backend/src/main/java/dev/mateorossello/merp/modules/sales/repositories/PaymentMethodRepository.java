package dev.mateorossello.merp.modules.sales.repositories;

import dev.mateorossello.merp.modules.sales.models.PaymentMethod;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface which extends JpaRepository to provide CRUD operations for PaymentMethod entities.
 */

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    Optional<PaymentMethod> findByName(String name);
    boolean existsByName(String name);
}
