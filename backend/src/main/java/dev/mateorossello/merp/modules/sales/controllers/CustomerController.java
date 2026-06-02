package dev.mateorossello.merp.modules.sales.controllers;

import dev.mateorossello.merp.modules.sales.dtos.CustomerInput;
import dev.mateorossello.merp.modules.sales.dtos.CustomerOutput;
import dev.mateorossello.merp.modules.sales.services.CustomerService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for managing Customer entities. Provides endpoints for creating, deleting, updating and retrieving customers.
 */

@RestController
@RequestMapping("/sales/customers")
@AllArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    //
    // Create methods
    //

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGE_CUSTOMERS')")
    public ResponseEntity<CustomerOutput> createCustomer(@Valid @RequestBody CustomerInput customerInput) {
        return ResponseEntity.ok(customerService.createCustomer(customerInput));
    }

    //
    // Delete methods
    //

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGE_CUSTOMERS')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    //
    // Update methods
    //

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGE_CUSTOMERS')")
    public ResponseEntity<CustomerOutput> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerInput customerInput) {
        return ResponseEntity.ok(customerService.updateCustomer(id, customerInput));
    }

    //
    // Get methods
    //

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_CUSTOMERS')")
    public ResponseEntity<CustomerOutput> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerDtoById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_CUSTOMERS')")
    public ResponseEntity<List<CustomerOutput>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/legal-name/{legalName}")
    @PreAuthorize("hasAuthority('VIEW_CUSTOMERS')")
    public ResponseEntity<CustomerOutput> getCustomerByLegalName(@PathVariable String legalName) {
        return ResponseEntity.ok(customerService.getCustomerByLegalName(legalName));
    }

    @GetMapping("/cuit/{cuit}")
    @PreAuthorize("hasAuthority('VIEW_CUSTOMERS')")
    public ResponseEntity<CustomerOutput> getCustomerByCuit(@PathVariable String cuit) {
        return ResponseEntity.ok(customerService.getCustomerByCuit(cuit));
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasAuthority('VIEW_CUSTOMERS')")
    public ResponseEntity<CustomerOutput> getCustomerByEmail(@PathVariable String email) {
        return ResponseEntity.ok(customerService.getCustomerByEmail(email));
    }
}
