package dev.mateorossello.merp.AccountingModule.Repositories;

import dev.mateorossello.merp.AccountingModule.Models.JournalEntryLine;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JournalEntryLineRepository extends JpaRepository<JournalEntryLine, Long> {
    List<JournalEntryLine> findByJournalEntryId(Long journalEntryId);
    List<JournalEntryLine> findByAccountId(Long accountId);
    @EntityGraph(attributePaths = {"journalEntry"})
    @Query("SELECT journalEntryLine FROM JournalEntryLine journalEntryLine WHERE journalEntryLine.account.id = :accountId AND journalEntryLine.journalEntry.entryDate < :entryDate")
    List<JournalEntryLine> getInitialBalanceLines(@Param("accountId") Long accountId, @Param("entryDate") LocalDate entryDate);
    @EntityGraph(attributePaths = {"journalEntry"})
    @Query("SELECT journalEntryLine FROM JournalEntryLine journalEntryLine WHERE journalEntryLine.account.id = :accountId AND journalEntryLine.journalEntry.entryDate BETWEEN :startDate AND :endDate")
    List<JournalEntryLine> getMovementsBetweenDates(@Param("accountId") Long accountId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
