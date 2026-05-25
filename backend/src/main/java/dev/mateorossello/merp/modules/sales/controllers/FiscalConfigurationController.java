package dev.mateorossello.merp.modules.sales.controllers;

import dev.mateorossello.merp.modules.sales.dtos.FiscalConfigurationInput;
import dev.mateorossello.merp.modules.sales.dtos.FiscalConfigurationOutput;
import dev.mateorossello.merp.modules.sales.services.FiscalConfigurationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for managing FiscalConfiguration entities. Provides endpoints for creating, updating and retrieving the fiscal configuration.
 */

@RestController
@RequestMapping("/sales/fiscal-configuration")
@AllArgsConstructor
public class FiscalConfigurationController {
    private final FiscalConfigurationService fiscalConfigurationService;

    //
    // Create methods
    //

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGE_FISCAL_CONFIGURATION')")
    public ResponseEntity<FiscalConfigurationOutput> createFiscalConfiguration(@Valid @RequestBody FiscalConfigurationInput fiscalConfigurationInput) {
        return ResponseEntity.ok(fiscalConfigurationService.createFiscalConfiguration(fiscalConfigurationInput));
    }

    //
    // Update methods
    //

    @PutMapping
    @PreAuthorize("hasAuthority('MANAGE_FISCAL_CONFIGURATION')")
    public ResponseEntity<FiscalConfigurationOutput> updateFiscalConfiguration(@Valid @RequestBody FiscalConfigurationInput fiscalConfigurationInput) {
        return ResponseEntity.ok(fiscalConfigurationService.updateFiscalConfiguration(fiscalConfigurationInput));
    }

    //
    // Get methods
    //

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_FISCAL_CONFIGURATION')")
    public ResponseEntity<FiscalConfigurationOutput> getFiscalConfiguration() {
        return ResponseEntity.ok(fiscalConfigurationService.getFiscalConfigurationDto());
    }
}
