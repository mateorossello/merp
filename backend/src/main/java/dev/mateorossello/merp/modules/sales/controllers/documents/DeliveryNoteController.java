package dev.mateorossello.merp.modules.sales.controllers.documents;

import dev.mateorossello.merp.modules.sales.dtos.documents.DeliveryNoteInput;
import dev.mateorossello.merp.modules.sales.dtos.documents.DeliveryNoteOutput;
import dev.mateorossello.merp.modules.sales.services.documents.DeliveryNoteService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for managing DeliveryNote entities. Provides endpoints for creating, updating and retrieving delivery notes.
 */

@RestController
@RequestMapping("/sales/delivery-notes")
@AllArgsConstructor
public class DeliveryNoteController {
    private final DeliveryNoteService deliveryNoteService;

    //
    // Create methods
    //

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGE_DELIVERY_NOTES')")
    public ResponseEntity<DeliveryNoteOutput> createDeliveryNote(@Valid @RequestBody DeliveryNoteInput deliveryNoteInput) {
        return ResponseEntity.ok(deliveryNoteService.createDeliveryNote(deliveryNoteInput));
    }

    //
    // Update methods
    //

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('MANAGE_DELIVERY_NOTES')")
    public ResponseEntity<DeliveryNoteOutput> cancelDeliveryNote(@PathVariable Long id, @RequestParam String reason) {
        return ResponseEntity.ok(deliveryNoteService.cancelDeliveryNote(id, reason));
    }

    //
    // Get methods
    //

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_DELIVERY_NOTES')")
    public ResponseEntity<DeliveryNoteOutput> getDeliveryNoteById(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryNoteService.getDeliveryNoteDtoById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_DELIVERY_NOTES')")
    public ResponseEntity<List<DeliveryNoteOutput>> getAllDeliveryNotes() {
        return ResponseEntity.ok(deliveryNoteService.getAllDeliveryNotes());
    }

    @GetMapping("/number/{number}")
    @PreAuthorize("hasAuthority('VIEW_DELIVERY_NOTES')")
    public ResponseEntity<DeliveryNoteOutput> getDeliveryNoteByNumber(@PathVariable Long number) {
        return ResponseEntity.ok(deliveryNoteService.getDeliveryNoteByNumber(number));
    }

    @GetMapping("/issue-date/{issueDate}")
    @PreAuthorize("hasAuthority('VIEW_DELIVERY_NOTES')")
    public ResponseEntity<List<DeliveryNoteOutput>> getDeliveryNotesByIssueDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issueDate) {
        return ResponseEntity.ok(deliveryNoteService.getDeliveryNotesByIssueDate(issueDate));
    }

    @GetMapping("/transaction/{transactionId}")
    @PreAuthorize("hasAuthority('VIEW_DELIVERY_NOTES')")
    public ResponseEntity<List<DeliveryNoteOutput>> getDeliveryNotesByTransactionId(@PathVariable Long transactionId) {
        return ResponseEntity.ok(deliveryNoteService.getDeliveryNoteDtosByTransactionId(transactionId));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('VIEW_DELIVERY_NOTES')")
    public ResponseEntity<List<DeliveryNoteOutput>> getDeliveryNotesByCreatedByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(deliveryNoteService.getDeliveryNotesByCreatedByUserId(userId));
    }

    @GetMapping("/delivery-date/{deliveryDate}")
    @PreAuthorize("hasAuthority('VIEW_DELIVERY_NOTES')")
    public ResponseEntity<List<DeliveryNoteOutput>> getDeliveryNotesByDeliveryDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deliveryDate) {
        return ResponseEntity.ok(deliveryNoteService.getDeliveryNotesByDeliveryDate(deliveryDate));
    }
}
