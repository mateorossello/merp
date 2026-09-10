package dev.mateorossello.merp.modules.sales.controllers;

import dev.mateorossello.merp.modules.sales.dtos.TransactionInput;
import dev.mateorossello.merp.modules.sales.dtos.TransactionOutput;
import dev.mateorossello.merp.modules.sales.services.TransactionService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
 * Controller class for managing Transaction entities. Provides methods for creating, updating, and retrieving transactions.
 */

@RestController
@RequestMapping("/sales/transactions")
@AllArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    //
    // Create methods
    //

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGE_TRANSACTIONS')")
    public ResponseEntity<TransactionOutput> createTransaction(@Valid @RequestBody TransactionInput transactionInput) {
        return ResponseEntity.ok(transactionService.createTransaction(transactionInput));
    }

    //
    // Update methods
    //
    
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('MANAGE_TRANSACTIONS')")
    public ResponseEntity<TransactionOutput> cancelTransaction(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.cancelTransaction(id));
    }

    //
    // Get methods
    //

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_TRANSACTIONS')")
    public ResponseEntity<TransactionOutput> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionDtoById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_TRANSACTIONS')")
    public ResponseEntity<List<TransactionOutput>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/issue-date/{issueDate}")
    @PreAuthorize("hasAuthority('VIEW_TRANSACTIONS')")
    public ResponseEntity<List<TransactionOutput>> getTransactionsByIssueDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issueDate) {
        return ResponseEntity.ok(transactionService.getTransactionsByIssueDate(issueDate));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('VIEW_TRANSACTIONS')")
    public ResponseEntity<List<TransactionOutput>> getTransactionsByCreatedByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionService.getTransactionsByCreatedByUserId(userId));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAuthority('VIEW_TRANSACTIONS')")
    public ResponseEntity<List<TransactionOutput>> getTransactionsByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(transactionService.getTransactionsByCustomerId(customerId));
    }

    @GetMapping("/receipt-number/{receiptNumber}")
    @PreAuthorize("hasAuthority('VIEW_TRANSACTIONS')")
    public ResponseEntity<TransactionOutput> getTransactionByReceiptNumber(@PathVariable Long receiptNumber) {
        return ResponseEntity.ok(transactionService.getTransactionByReceiptNumber(receiptNumber));
    }

    @GetMapping("/reports/daily")
    @PreAuthorize("hasAuthority('VIEW_TRANSACTIONS')")
    public ResponseEntity<Map<String, Object>> getSalesReportByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(transactionService.getSalesReportByDate(date));
    }

    @GetMapping("/reports/range")
    @PreAuthorize("hasAuthority('VIEW_TRANSACTIONS')")
    public ResponseEntity<Map<String, Object>> getSalesReportBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(transactionService.getSalesReportBetweenDates(startDate, endDate));
    }
}
