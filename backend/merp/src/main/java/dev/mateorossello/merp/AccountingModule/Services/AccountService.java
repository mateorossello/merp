package dev.mateorossello.merp.AccountingModule.Services;

import dev.mateorossello.merp.AccountingModule.DTOs.AccountInput;
import dev.mateorossello.merp.AccountingModule.DTOs.AccountOutput;
import dev.mateorossello.merp.AccountingModule.Mappers.AccountMapper;
import dev.mateorossello.merp.AccountingModule.Models.Account;
import dev.mateorossello.merp.AccountingModule.Models.AccountType;
import dev.mateorossello.merp.AccountingModule.Repositories.AccountRepository;
import dev.mateorossello.merp.Exceptions.ResourceConflictException;
import dev.mateorossello.merp.Exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for managing accounts. Provides methods for creating, deleting, updating and retrieving accounts.
 */

@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final JournalEntryLineService journalEntryLineService;

    // Create methods

    @Transactional
    public AccountOutput createAccount(AccountInput newAccount) {
        if(!newAccount.code().matches("^\\d+(\\.\\d+)*$")) {
            throw new ResourceConflictException("Account not created, code must follow hierarchical format with numbers separated by dots");
        }

        if(existsByCode(newAccount.code())) {
            throw new ResourceConflictException("Account not created, another account with the same code already exists");
        }

        if(existsByName(newAccount.name())) {
            throw new ResourceConflictException("Account not created, another account with the same name already exists");
        }
        
        Account account;

        if(newAccount.parentAccountId() != null) {
            Account parentAccount = accountRepository.findById(newAccount.parentAccountId()).orElseThrow(() -> new ResourceNotFoundException("Parent account not found"));

            if (!newAccount.code().matches("^" + Pattern.quote(parentAccount.getCode()) + "\\.\\d+$")) {
                throw new ResourceConflictException("Account not created, code must be a direct child of the parent account");
            }

            if (journalEntryLineService.existsByAccountId(parentAccount.getId())) {
                throw new ResourceConflictException("Account not created, parent account has journal entry lines associated");
            }

            parentAccount.setReceiveBalance(false);
            accountRepository.save(parentAccount);

            account = Account.builder().parentAccount(parentAccount).code(newAccount.code()).name(newAccount.name()).description(newAccount.description()).build();
        } else {
            if (newAccount.type() == null) {
                throw new IllegalArgumentException("Account type or parent account must be provided");
            }

            account = Account.builder().code(newAccount.code()).type(newAccount.type()).name(newAccount.name()).description(newAccount.description()).build();
        }

        return accountMapper.toOutput(accountRepository.save(account));
    }

    // Delete methods

    @Transactional
    public void deleteAccount(Long id) {
        if(existsByParentAccountId(id)) {
            throw new ResourceConflictException("Account not deleted, has child accounts associated");
        }

        Account account = getAccountById(id);

        if(journalEntryLineService.existsByAccountId(account.getId())) {
            account.setState(false);
            accountRepository.save(account);
        } else {
            accountRepository.delete(account);
        }

        Account parentAccount = account.getParentAccount();

        if(parentAccount != null && !existsByParentAccountId(parentAccount.getId())) {
            parentAccount.setReceiveBalance(true);
            accountRepository.save(parentAccount);
        }
    }

    // Update methods

    @Transactional
    public void updateAccount(Long id, String name, String description) {
        Account account = getAccountById(id);

        if(!account.getName().equals(name) && existsByName(name)) {
            throw new ResourceConflictException("Account not modified, another account with the same name already exists");
        }

        account.setName(name);
        account.setDescription(description);

        accountRepository.save(account);
    }

    //
    // Get methods
    //

    public Account getAccountById(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }

    public AccountOutput getAccountDtoById(Long id) {
        return accountMapper.toOutput(getAccountById(id));
    }

    // Parent account related methods

    public boolean existsByParentAccountId(Long parentAccountId) {
        return accountRepository.existsByParentAccountId(parentAccountId);
    }

    // Code related methods

    public Account getAccountByCode(String code) {
        return accountRepository.findByCode(code).orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }

    public AccountOutput getAccountDtoByCode(String code) {
        return accountMapper.toOutput(getAccountByCode(code));
    }

    public boolean existsByCode(String code) {
        return accountRepository.existsByCode(code);
    }

    public Account getAccountByCodeWithParentAccount(String code) {
        return accountRepository.findFullByCode(code).orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }

    public AccountOutput getAccountDtoByCodeWithParentAccount(String code) {
        return accountMapper.toOutput(getAccountByCodeWithParentAccount(code));
    }

    // Type related methods

    public List<Account> getAccountsByType(AccountType type) {
        return accountRepository.findAllByType(type);
    }

    public List<AccountOutput> getAccountDtosByType(AccountType type) {
        return accountMapper.toOutputList(getAccountsByType(type));
    }

    // Name related methods

    public Account getAccountByName(String name) {
        return accountRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }

    public AccountOutput getAccountDtoByName(String name) {
        return accountMapper.toOutput(getAccountByName(name));
    }

    public boolean existsByName(String name) {
        return accountRepository.existsByName(name);
    }

    // State and receive balance related methods

    public List<Account> getAllAccountsByReceiveBalance(boolean receiveBalance) {
        return accountRepository.findAllByReceiveBalance(receiveBalance);
    }

    public List<AccountOutput> getAllAccountDtosByReceiveBalance(boolean receiveBalance) {
        return accountMapper.toOutputList(getAllAccountsByReceiveBalance(receiveBalance));
    }

    public List<Account> getAllAccountsByState(boolean state) {
        return accountRepository.findAllByState(state);
    }

    public List<AccountOutput> getAllAccountDtosByState(boolean state) {
        return accountMapper.toOutputList(getAllAccountsByState(state));
    }

    public List<Account> getAllAccountsByReceiveBalanceAndState(boolean receiveBalance, boolean state) {
        return accountRepository.findAllByReceiveBalanceAndState(receiveBalance, state);
    }

    public List<AccountOutput> getAllAccountDtosByReceiveBalanceAndState(boolean receiveBalance, boolean state) {
        return accountMapper.toOutputList(getAllAccountsByReceiveBalanceAndState(receiveBalance, state));
    }

    // Other methods

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public List<AccountOutput> getAllAccountDtos() {
        return accountMapper.toOutputList(getAllAccounts());
    }
}
