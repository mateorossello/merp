package dev.mateorossello.merp.modules.sales.controllers.documents;

import dev.mateorossello.merp.configuration.CustomUser;
import dev.mateorossello.merp.modules.sales.dtos.documents.InvoiceInput;
import dev.mateorossello.merp.modules.sales.dtos.documents.InvoiceOutput;
import dev.mateorossello.merp.modules.sales.services.documents.InvoiceService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for managing Invoice entities. Provide endpoints for creating and retrieving invoices.
 */

@RestController
@RequestMapping("/sales/invoices")
@AllArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;

    //
    // Create methods
    //

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGE_INVOICES')")
    public ResponseEntity<InvoiceOutput> createInvoice(@AuthenticationPrincipal CustomUser user, @Valid @RequestBody InvoiceInput invoiceInput) {
        return ResponseEntity.ok(invoiceService.createInvoice(user.getId(), invoiceInput));
    }

    //
    // Get methods
    //

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_INVOICES')")
    public ResponseEntity<InvoiceOutput> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceDtoById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_INVOICES')")
    public ResponseEntity<List<InvoiceOutput>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/number/{number}")
    @PreAuthorize("hasAuthority('VIEW_INVOICES')")
    public ResponseEntity<InvoiceOutput> getInvoiceByNumber(@PathVariable Long number) {
        return ResponseEntity.ok(invoiceService.getInvoiceByNumber(number));
    }

    @GetMapping("/issue-date/{issueDate}")
    @PreAuthorize("hasAuthority('VIEW_INVOICES')")
    public ResponseEntity<List<InvoiceOutput>> getInvoicesByIssueDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issueDate) {
        return ResponseEntity.ok(invoiceService.getInvoicesByIssueDate(issueDate));
    }

    @GetMapping("/transaction/{transactionId}")
    @PreAuthorize("hasAuthority('VIEW_INVOICES')")
    public ResponseEntity<List<InvoiceOutput>> getInvoicesByTransactionId(@PathVariable Long transactionId) {
        return ResponseEntity.ok(invoiceService.getInvoiceDtosByTransactionId(transactionId));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('VIEW_INVOICES')")
    public ResponseEntity<List<InvoiceOutput>> getInvoicesCreatedByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByCreatedByUserId(userId));
    }

    @GetMapping("/due-date/{dueDate}")
    @PreAuthorize("hasAuthority('VIEW_INVOICES')")
    public ResponseEntity<List<InvoiceOutput>> getInvoicesByDueDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
        return ResponseEntity.ok(invoiceService.getInvoicesByDueDate(dueDate));
    }
}
