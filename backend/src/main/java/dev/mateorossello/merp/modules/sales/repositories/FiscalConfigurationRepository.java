package dev.mateorossello.merp.modules.sales.repositories;

import dev.mateorossello.merp.modules.sales.models.FiscalConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface which extends JpaRepository to provide CRUD operations for FiscalConfiguration entities.
 */

public interface FiscalConfigurationRepository extends JpaRepository<FiscalConfiguration, Long> {
    
}
