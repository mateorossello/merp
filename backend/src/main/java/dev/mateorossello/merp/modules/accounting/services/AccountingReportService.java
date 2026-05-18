package dev.mateorossello.merp.modules.accounting.services;

import dev.mateorossello.merp.modules.accounting.dtos.AccountMovementOutput;
import dev.mateorossello.merp.modules.accounting.dtos.GeneralJournalOutput;
import dev.mateorossello.merp.modules.accounting.dtos.GeneralLedgerOutput;
import dev.mateorossello.merp.modules.accounting.dtos.JournalEntryOutput;
import dev.mateorossello.merp.modules.accounting.models.Account;
import dev.mateorossello.merp.modules.accounting.models.AccountType;
import dev.mateorossello.merp.modules.accounting.models.JournalEntryLine;
import dev.mateorossello.merp.modules.accounting.utils.DateRangeValidator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AccountingReportService {
    private final AccountService accountService;
    private final JournalEntryService journalEntryService;
    private final JournalEntryLineService journalEntryLineService;

    public AccountingReportService(AccountService accountService, JournalEntryService journalEntryService, JournalEntryLineService journalEntryLineService) {
        this.accountService = accountService;
        this.journalEntryService = journalEntryService;
        this.journalEntryLineService = journalEntryLineService;
    }

    // Calculation methods
    // These methods are used to calculate balances and other values for creating the reports

    private BigDecimal calculateBalanceForLines(List<JournalEntryLine> lines, AccountType type, BigDecimal startingBalance) {
        BigDecimal balance = startingBalance;
        for (JournalEntryLine line : lines) {
            BigDecimal debit = line.isDebit() ? line.getAmount() : BigDecimal.ZERO;
            BigDecimal credit = !line.isDebit() ? line.getAmount() : BigDecimal.ZERO;
            balance = updateBalance(balance, debit, credit, type);
        }
        return balance;
    }

    private BigDecimal updateBalance(BigDecimal currentBalance, BigDecimal debit, BigDecimal credit, AccountType type) {
        if (type == AccountType.ASSET || type == AccountType.EXPENSE) {
            return currentBalance.add(debit).subtract(credit);
        } else {
            return currentBalance.add(credit).subtract(debit);
        }
    }

    //
    // Get methods
    //

    // General Journal

    public GeneralJournalOutput getGeneralJournal(LocalDate startDate, LocalDate endDate) {
        DateRangeValidator.validate(startDate, endDate);
        List<JournalEntryOutput> entries = journalEntryService.getAllJournalEntryDtosByEntryDateBetween(startDate, endDate);
        return new GeneralJournalOutput(entries);
    }

    // General Ledger

    public GeneralLedgerOutput getGeneralLedger(Long accountId, LocalDate startDate, LocalDate endDate) {
        DateRangeValidator.validate(startDate, endDate);
        Account account = accountService.getAccountById(accountId);

        List<JournalEntryLine> initialLines = journalEntryLineService.getInitialBalanceLines(accountId, startDate);
        BigDecimal initialBalance = calculateBalanceForLines(initialLines, account.getType(), BigDecimal.ZERO);

        List<JournalEntryLine> periodLines = journalEntryLineService.getMovementsBetweenDates(accountId, startDate, endDate);
        List<AccountMovementOutput> movements = new ArrayList<>();
        
        BigDecimal currentBalance = initialBalance;

        for (JournalEntryLine journalEntryLine : periodLines) {
            BigDecimal debit = journalEntryLine.isDebit() ? journalEntryLine.getAmount() : BigDecimal.ZERO;
            BigDecimal credit = !journalEntryLine.isDebit() ? journalEntryLine.getAmount() : BigDecimal.ZERO;

            currentBalance = updateBalance(currentBalance, debit, credit, account.getType());

            movements.add(new AccountMovementOutput(
                journalEntryLine.getJournalEntry().getEntryDate(),
                journalEntryLine.getJournalEntry().getDescription(),
                debit,
                credit,
                currentBalance
            ));
        }

        return new GeneralLedgerOutput(
            account.getId(),
            account.getName(),
            startDate,
            endDate,
            initialBalance,
            movements
        );
    }
}
