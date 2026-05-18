package dev.mateorossello.merp.modules.accounting.controllers;

import dev.mateorossello.merp.modules.accounting.dtos.GeneralJournalOutput;
import dev.mateorossello.merp.modules.accounting.dtos.GeneralLedgerOutput;
import dev.mateorossello.merp.modules.accounting.services.AccountingReportService;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
public class AccountingReportController {
    private final AccountingReportService accountingReportService;

    public AccountingReportController(AccountingReportService accountingReportService) {
        this.accountingReportService = accountingReportService;
    }

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
