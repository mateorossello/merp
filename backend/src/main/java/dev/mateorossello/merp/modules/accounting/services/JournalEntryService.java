package dev.mateorossello.merp.modules.accounting.services;

import dev.mateorossello.merp.exceptions.ResourceConflictException;
import dev.mateorossello.merp.exceptions.ResourceNotFoundException;
import dev.mateorossello.merp.modules.accounting.dtos.JournalEntryInput;
import dev.mateorossello.merp.modules.accounting.dtos.JournalEntryLineInput;
import dev.mateorossello.merp.modules.accounting.dtos.JournalEntryOutput;
import dev.mateorossello.merp.modules.accounting.mappers.JournalEntryMapper;
import dev.mateorossello.merp.modules.accounting.models.Account;
import dev.mateorossello.merp.modules.accounting.models.JournalEntry;
import dev.mateorossello.merp.modules.accounting.models.JournalEntryLine;
import dev.mateorossello.merp.modules.accounting.repositories.JournalEntryRepository;
import dev.mateorossello.merp.modules.accounting.utils.DateRangeValidator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing journal entries. Provides methods for creating, deleting, updating and retrieving journal entries.
 */

@Service
@AllArgsConstructor
public class JournalEntryService {
    private final JournalEntryRepository journalEntryRepository;
    private final JournalEntryMapper journalEntryMapper;
    private final AccountService accountService;

    // Validation methods
    // These methods are used to validate the input data for creating journal entries

    private void validateEntryDate(LocalDate entryDate) {
        if (entryDate.isAfter(LocalDate.now())) {
            throw new ResourceConflictException("Journal entry date cannot be in the future.");
        }

        journalEntryRepository.findFirstByOrderByEntryDateDesc().ifPresent(lastEntry -> {
            if (entryDate.isBefore(lastEntry.getEntryDate())) {
                throw new ResourceConflictException("Journal entry date cannot be strictly before the last recorded entry date.");
            }
        });
    }

    private void validateBalanceAndUniqueAccounts(List<JournalEntryLineInput> journalEntryLineInputs) {
        if (journalEntryLineInputs == null || journalEntryLineInputs.size() < 2) {
            throw new ResourceConflictException("A journal entry must have at least two lines.");
        }

        BigDecimal totalDebits = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;
        Set<Long> accountIds = new HashSet<>();

        for (JournalEntryLineInput journalEntryLine : journalEntryLineInputs) {
            if (!accountIds.add(journalEntryLine.accountId())) {
                throw new ResourceConflictException("Duplicate accounts are not allowed within the same journal entry.");
            }

            if (journalEntryLine.amount() == null || journalEntryLine.amount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ResourceConflictException("Journal entry line amounts must be strictly greater than zero.");
            }

            if (journalEntryLine.debit()) {
                totalDebits = totalDebits.add(journalEntryLine.amount());
            } else {
                totalCredits = totalCredits.add(journalEntryLine.amount());
            }
        }

        if (totalDebits.compareTo(totalCredits) != 0) {
            throw new ResourceConflictException(String.format("Journal entry is not balanced, total debits: %s, total credits: %s.", totalDebits, totalCredits));
        }
    }

    // Create methods

    @Transactional
    public JournalEntryOutput createJournalEntry(JournalEntryInput newJournalEntry) {
        validateEntryDate(newJournalEntry.entryDate());
        validateBalanceAndUniqueAccounts(newJournalEntry.journalEntryLinesInput());

        List<Long> accountIds = newJournalEntry.journalEntryLinesInput().stream().map(JournalEntryLineInput::accountId).toList();
        Map<Long, Account> accountMap = accountService.getAccountsByIds(accountIds).stream().collect(Collectors.toMap(Account::getId, account -> account));

        if (accountMap.size() != accountIds.size()) {
            throw new ResourceNotFoundException("One or more accounts not found.");
        }

        JournalEntry journalEntry = journalEntryMapper.toEntity(newJournalEntry);

        for (int i = 0; i < newJournalEntry.journalEntryLinesInput().size(); i++) {
            JournalEntryLineInput lineInput = newJournalEntry.journalEntryLinesInput().get(i);
            JournalEntryLine lineEntity = journalEntry.getJournalEntryLines().get(i);

            Account account = accountMap.get(lineInput.accountId());

            if (!account.isReceiveBalance() || !account.isState()) {
                throw new ResourceConflictException("Account '" + account.getName() + "' cannot receive balances or is inactive.");
            }

            lineEntity.setAccount(account);
            lineEntity.setJournalEntry(journalEntry);
        }

        JournalEntry journalEntrySaved = journalEntryRepository.save(journalEntry);

        return journalEntryMapper.toOutput(journalEntrySaved);
    }

    //
    // Get methods
    //

    public JournalEntry getJournalEntryById(Long id) {
        return journalEntryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Journal entry not found."));
    }

    public JournalEntryOutput getJournalEntryDtoById(Long id) {
        return journalEntryMapper.toOutput(getJournalEntryById(id));
    }

    // Entry date related methods

    public List<JournalEntry> getAllJournalEntriesByEntryDate(LocalDate entryDate) {
        return journalEntryRepository.findAllByEntryDate(entryDate);
    }

    public List<JournalEntryOutput> getAllJournalEntryDtosByEntryDate(LocalDate entryDate) {
        return journalEntryMapper.toOutputList(getAllJournalEntriesByEntryDate(entryDate));
    }

    public List<JournalEntry> getAllJournalEntriesByEntryDateBetween(LocalDate startDate, LocalDate endDate) {
        DateRangeValidator.validate(startDate, endDate);
        return journalEntryRepository.findAllByEntryDateBetween(startDate, endDate);
    }

    public List<JournalEntryOutput> getAllJournalEntryDtosByEntryDateBetween(LocalDate startDate, LocalDate endDate) {
        return journalEntryMapper.toOutputList(getAllJournalEntriesByEntryDateBetween(startDate, endDate));
    }

    public JournalEntry getJournalEntryByLatestDate() {
        return journalEntryRepository.findFirstByOrderByEntryDateDesc().orElseThrow(() -> new ResourceNotFoundException("No journal entries found."));
    }

    public JournalEntryOutput getJournalEntryDtoByLatestDate() {
        return journalEntryMapper.toOutput(getJournalEntryByLatestDate());
    }

    // Created at related methods

    public List<JournalEntry> getAllJournalEntriesByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        DateRangeValidator.validate(startDate, endDate);
        return journalEntryRepository.findAllByCreatedAtBetween(startDate, endDate);
    }

    public List<JournalEntryOutput> getAllJournalEntryDtosByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return journalEntryMapper.toOutputList(getAllJournalEntriesByCreatedAtBetween(startDate, endDate));
    }

    // Created by user related methods

    public List<JournalEntry> getAllJournalEntriesByUserId(Long userId) {
        return journalEntryRepository.findAllByCreatedByUserId(userId);
    }

    public List<JournalEntryOutput> getAllJournalEntryDtosByUserId(Long userId) {
        return journalEntryMapper.toOutputList(getAllJournalEntriesByUserId(userId));
    }

    // Other methods

    public List<JournalEntry> getAllJournalEntries() {
        return journalEntryRepository.findAll();
    }

    public List<JournalEntryOutput> getAllJournalEntryDtos() {
        return journalEntryMapper.toOutputList(getAllJournalEntries());
    }
}
