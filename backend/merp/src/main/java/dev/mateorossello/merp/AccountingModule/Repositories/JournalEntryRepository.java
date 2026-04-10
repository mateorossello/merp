package dev.mateorossello.merp.AccountingModule.Repositories;

import dev.mateorossello.merp.AccountingModule.Models.JournalEntry;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
    @EntityGraph(attributePaths = {"journalEntryLines", "journalEntryLines.account"})
    List<JournalEntry> findAllByEntryDate(LocalDate entryDate);
    @EntityGraph(attributePaths = {"journalEntryLines", "journalEntryLines.account"})
    List<JournalEntry> findAllByEntryDateBetween(LocalDate startDate, LocalDate endDate);
    @EntityGraph(attributePaths = {"journalEntryLines", "journalEntryLines.account"})
    Optional<JournalEntry> findFirstByOrderByEntryDateDesc();
    List<JournalEntry> findAllByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<JournalEntry> findAllByCreatedByUserId(Long createdByUserId);
}
