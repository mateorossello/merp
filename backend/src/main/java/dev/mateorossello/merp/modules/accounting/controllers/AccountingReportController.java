package dev.mateorossello.merp.modules.accounting.controllers;

import dev.mateorossello.merp.modules.accounting.dtos.GeneralJournalOutput;
import dev.mateorossello.merp.modules.accounting.dtos.GeneralLedgerOutput;
import dev.mateorossello.merp.modules.accounting.services.AccountingReportService;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for managing accounting reports. Provides methods for retrieving general journal and general ledger.
 */

@RestController
@RequestMapping("/reports")
@AllArgsConstructor
public class AccountingReportController {
    private final AccountingReportService accountingReportService;

    //
    // Get methods
    //

    @GetMapping("/general-journal")
    @PreAuthorize("hasAuthority('VIEW_REPORTS')")
    public ResponseEntity<GeneralJournalOutput> getGeneralJournal(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(accountingReportService.getGeneralJournal(startDate, endDate));
    }

    @GetMapping("/general-ledger")
    @PreAuthorize("hasAuthority('VIEW_REPORTS')")
    public ResponseEntity<GeneralLedgerOutput> getGeneralLedger(
        @RequestParam Long accountId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(accountingReportService.getGeneralLedger(accountId, startDate, endDate));
    }
}
