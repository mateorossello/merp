package dev.mateorossello.merp.modules.accounting.controllers;

import dev.mateorossello.merp.modules.accounting.dtos.JournalEntryInput;
import dev.mateorossello.merp.modules.accounting.dtos.JournalEntryOutput;
import dev.mateorossello.merp.modules.accounting.services.JournalEntryService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for managing journal entries. Provides methods for creating, deleting, updating and retrieving journal entries.
 */

@RestController
@RequestMapping("/journal-entries")
@AllArgsConstructor
public class JournalEntryController {
    private final JournalEntryService journalEntryService;

    // Create methods

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGE_JOURNAL_ENTRIES')")
    public ResponseEntity<JournalEntryOutput> createJournalEntry(@Valid @RequestBody JournalEntryInput newJournalEntry) {
        return ResponseEntity.ok(journalEntryService.createJournalEntry(newJournalEntry));
    }

    //
    // Get methods
    //

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_JOURNAL_ENTRIES')")
    public ResponseEntity<JournalEntryOutput> getJournalEntryById(@PathVariable Long id) {
        return ResponseEntity.ok(journalEntryService.getJournalEntryDtoById(id));
    }

    // Entry date related methods

    @GetMapping("/by-date")
    @PreAuthorize("hasAuthority('VIEW_JOURNAL_ENTRIES')")
    public ResponseEntity<List<JournalEntryOutput>> getAllJournalEntriesByEntryDate(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate entryDate) {
        return ResponseEntity.ok(journalEntryService.getAllJournalEntryDtosByEntryDate(entryDate));
    }

    @GetMapping("/between-dates")
    @PreAuthorize("hasAuthority('VIEW_JOURNAL_ENTRIES')")
    public ResponseEntity<List<JournalEntryOutput>> getAllJournalEntriesByEntryDateBetween(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(journalEntryService.getAllJournalEntryDtosByEntryDateBetween(startDate, endDate));
    }

    @GetMapping("/latest")
    @PreAuthorize("hasAuthority('VIEW_JOURNAL_ENTRIES')")
    public ResponseEntity<JournalEntryOutput> getJournalEntryByLatestDate() {
        return ResponseEntity.ok(journalEntryService.getJournalEntryDtoByLatestDate());
    }

    // Created at related methods

    @GetMapping("/created-between")
    @PreAuthorize("hasAuthority('VIEW_JOURNAL_ENTRIES')")
    public ResponseEntity<List<JournalEntryOutput>> getAllJournalEntriesByCreatedAtBetween(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(journalEntryService.getAllJournalEntryDtosByCreatedAtBetween(startDate, endDate));
    }

    // Other methods

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_JOURNAL_ENTRIES')")
    public ResponseEntity<List<JournalEntryOutput>> getAllJournalEntries(@RequestParam(required = false) Long createdByUserId) {
        return ResponseEntity.ok(
            createdByUserId != null
                ? journalEntryService.getAllJournalEntryDtosByUserId(createdByUserId)
                : journalEntryService.getAllJournalEntryDtos()
        );
    }
}
