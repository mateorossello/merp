package dev.mateorossello.merp.modules.sales.controllers;

import dev.mateorossello.merp.modules.sales.dtos.PaymentMethodInput;
import dev.mateorossello.merp.modules.sales.dtos.PaymentMethodOutput;
import dev.mateorossello.merp.modules.sales.services.PaymentMethodService;
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
 * Controller class for managing PaymentMethod entities.
 */

@RestController
@RequestMapping("/sales/payment-methods")
@AllArgsConstructor
public class PaymentMethodController {
    private final PaymentMethodService paymentMethodService;

    //
    // Create methods
    //

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGE_PAYMENT_METHODS')")
    public ResponseEntity<PaymentMethodOutput> createPaymentMethod(@Valid @RequestBody PaymentMethodInput paymentMethodInput) {
        return ResponseEntity.ok(paymentMethodService.createPaymentMethod(paymentMethodInput));
    }

    //
    // Delete methods
    //

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGE_PAYMENT_METHODS')")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable Long id) {
        paymentMethodService.deletePaymentMethod(id);
        return ResponseEntity.noContent().build();
    }

    //
    // Update methods
    //
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGE_PAYMENT_METHODS')")
    public ResponseEntity<PaymentMethodOutput> updatePaymentMethod(@PathVariable Long id, @Valid @RequestBody PaymentMethodInput paymentMethodInput) {
        return ResponseEntity.ok(paymentMethodService.updatePaymentMethod(id, paymentMethodInput));
    }

    //
    // Get methods
    //

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_PAYMENT_METHODS')")
    public ResponseEntity<PaymentMethodOutput> getPaymentMethodById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentMethodService.getPaymentMethodDtoById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_PAYMENT_METHODS')")
    public ResponseEntity<List<PaymentMethodOutput>> getAllPaymentMethods() {
        return ResponseEntity.ok(paymentMethodService.getAllPaymentMethods());
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasAuthority('VIEW_PAYMENT_METHODS')")
    public ResponseEntity<PaymentMethodOutput> getPaymentMethodByName(@PathVariable String name) {
        return ResponseEntity.ok(paymentMethodService.getPaymentMethodByName(name));
    }
}
