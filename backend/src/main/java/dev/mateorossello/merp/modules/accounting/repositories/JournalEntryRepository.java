package dev.mateorossello.merp.modules.accounting.repositories;

import dev.mateorossello.merp.modules.accounting.models.JournalEntry;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface which extends JpaRepository to provide CRUD operations for JournalEntry entities.
 */

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
    // Entry date related queries
    @EntityGraph(attributePaths = {"journalEntryLines", "journalEntryLines.account"})
    List<JournalEntry> findAllByEntryDate(LocalDate entryDate);
    @EntityGraph(attributePaths = {"journalEntryLines", "journalEntryLines.account"})
    List<JournalEntry> findAllByEntryDateBetween(LocalDate startDate, LocalDate endDate);
    @EntityGraph(attributePaths = {"journalEntryLines", "journalEntryLines.account"})
    Optional<JournalEntry> findFirstByOrderByEntryDateDesc();

    // Created at related queries
    List<JournalEntry> findAllByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Created by user related queries
    List<JournalEntry> findAllByCreatedByUserId(Long createdByUserId);
}
