package dev.mateorossello.merp.modules.sales.repositories;

import dev.mateorossello.merp.modules.sales.models.TransactionPaymentMethod;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface which extends JpaRepository to provide CRUD operations for TransactionPaymentMethod entities.
 */

public interface TransactionPaymentMethodRepository extends JpaRepository<TransactionPaymentMethod, Long> {
    List<TransactionPaymentMethod> findByPaymentMethodId(Long paymentMethodId);
    boolean existsByPaymentMethodId(Long paymentMethodId);
}
