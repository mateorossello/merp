package dev.mateorossello.merp.AccountingModule.Services;

import dev.mateorossello.merp.AccountingModule.DTOs.JournalEntryLineOutput;
import dev.mateorossello.merp.AccountingModule.Mappers.JournalEntryLineMapper;
import dev.mateorossello.merp.AccountingModule.Models.JournalEntryLine;
import dev.mateorossello.merp.AccountingModule.Repositories.JournalEntryLineRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for managing journal entry lines. Provides methods for creating, deleting, updating and retrieving journal entry lines.
 */

@Service
@AllArgsConstructor
public class JournalEntryLineService {
    private final JournalEntryLineRepository journalEntryLineRepository;
    private final JournalEntryLineMapper journalEntryLineMapper;

    //
    // Get methods
    //

    public JournalEntryLine getJournalEntryLineById(Long id) {
        return journalEntryLineRepository.findById(id).orElseThrow(() -> new RuntimeException("Journal entry line not found"));
    }

    public JournalEntryLineOutput getJournalEntryLineDtoById(Long id) {
        return journalEntryLineMapper.toOutput(getJournalEntryLineById(id));
    }

    // Journal entry related methods

    public List<JournalEntryLine> getJournalEntryLinesByJournalEntryId(Long journalEntryId) {
        return journalEntryLineRepository.findByJournalEntryId(journalEntryId);
    }

    public List<JournalEntryLineOutput> getJournalEntryLineDtosByJournalEntryId(Long journalEntryId) {
        return journalEntryLineMapper.toOutputList(getJournalEntryLinesByJournalEntryId(journalEntryId));
    }

    public boolean existsByJournalEntryId(Long journalEntryId) {
        return journalEntryLineRepository.existsByJournalEntryId(journalEntryId);
    }

    // Account related methods

    public List<JournalEntryLine> getJournalEntryLinesByAccountId(Long accountId) {
        return journalEntryLineRepository.findByAccountId(accountId);
    }

    public List<JournalEntryLineOutput> getJournalEntryLineDtosByAccountId(Long accountId) {
        return journalEntryLineMapper.toOutputList(getJournalEntryLinesByAccountId(accountId));
    }
        
    public boolean existsByAccountId(Long accountId) {
        return journalEntryLineRepository.existsByAccountId(accountId);
    }

    // Initial balance related methods

    public List<JournalEntryLine> getInitialBalanceLines(Long accountId, LocalDate entryDate) {
        return journalEntryLineRepository.getInitialBalanceLines(accountId, entryDate);
    }

    public List<JournalEntryLineOutput> getInitialBalanceLineDtos(Long accountId, LocalDate entryDate) {
        return journalEntryLineMapper.toOutputList(getInitialBalanceLines(accountId, entryDate));
    }

    // Movements between dates related methods

    public List<JournalEntryLine> getMovementsBetweenDates(Long accountId, LocalDate startDate, LocalDate endDate) {
        return journalEntryLineRepository.getMovementsBetweenDates(accountId, startDate, endDate);
    }

    public List<JournalEntryLineOutput> getMovementBetweenDatesDtos(Long accountId, LocalDate startDate, LocalDate endDate) {
        return journalEntryLineMapper.toOutputList(getMovementsBetweenDates(accountId, startDate, endDate));
    }
}
