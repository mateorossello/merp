package dev.mateorossello.merp.AccountingModule.Repositories;

import dev.mateorossello.merp.AccountingModule.Models.JournalEntryLine;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Interface which extends JpaRepository to provide CRUD operations for JournalEntryLine entities.
 */

public interface JournalEntryLineRepository extends JpaRepository<JournalEntryLine, Long> {
    // Journal entry related queries
    List<JournalEntryLine> findByJournalEntryId(Long journalEntryId);
    boolean existsByJournalEntryId(Long journalEntryId);

    // Account related queries
    List<JournalEntryLine> findByAccountId(Long accountId);
    boolean existsByAccountId(Long accountId);

    // Initial balance related queries
    @EntityGraph(attributePaths = {"journalEntry"})
    @Query("SELECT journalEntryLine FROM JournalEntryLine journalEntryLine WHERE journalEntryLine.account.id = :accountId AND journalEntryLine.journalEntry.entryDate < :entryDate")
    List<JournalEntryLine> getInitialBalanceLines(@Param("accountId") Long accountId, @Param("entryDate") LocalDate entryDate);
    
    // Movements between dates related queries
    @EntityGraph(attributePaths = {"journalEntry"})
    @Query("SELECT journalEntryLine FROM JournalEntryLine journalEntryLine WHERE journalEntryLine.account.id = :accountId AND journalEntryLine.journalEntry.entryDate BETWEEN :startDate AND :endDate")
    List<JournalEntryLine> getMovementsBetweenDates(@Param("accountId") Long accountId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
