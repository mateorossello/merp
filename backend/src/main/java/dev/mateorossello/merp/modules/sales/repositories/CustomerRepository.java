package dev.mateorossello.merp.modules.sales.repositories;

import dev.mateorossello.merp.modules.sales.models.Customer;
import dev.mateorossello.merp.modules.sales.models.FiscalType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface which extends JpaRepository to provide CRUD operations for Customer entities.
 */

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByLegalName(String legalName);
    boolean existsByLegalName(String legalName);
    Optional<Customer> findByCuit(String cuit);
    boolean existsByCuit(String cuit);
    Optional<Customer> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Customer> findAllByFiscalType(FiscalType fiscalType);
}
