package dev.mateorossello.merp.AccountingModule.Controllers;

import dev.mateorossello.merp.AccountingModule.DTOs.AccountInput;
import dev.mateorossello.merp.AccountingModule.DTOs.AccountOutput;
import dev.mateorossello.merp.AccountingModule.Models.AccountType;
import dev.mateorossello.merp.AccountingModule.Services.AccountService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for managing accounts. Provides methods for creating, deleting, updating and retrieving accounts.
 */

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
public class AccountController {
    private final AccountService accountService;

    // Create methods

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGE_ACCOUNTS')")
    public ResponseEntity<AccountOutput> createAccount(@Valid @RequestBody AccountInput newAccount) {
        return ResponseEntity.ok(accountService.createAccount(newAccount));
    }

    // Delete methods

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGE_ACCOUNTS')")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    // Update methods

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGE_ACCOUNTS')")
    public ResponseEntity<Void> updateAccount(@PathVariable Long id, @RequestParam String name, @RequestParam String description) {
        accountService.updateAccount(id, name, description);
        return ResponseEntity.noContent().build();
    }

    //
    // Get methods
    //

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_ACCOUNTS')")
    public ResponseEntity<AccountOutput> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountDtoById(id));
    }

    // Parent account related methods

    @GetMapping("/exists/parent-account")
    @PreAuthorize("hasAuthority('VIEW_ACCOUNTS')")
    public ResponseEntity<Boolean> existsByParentAccountId(@RequestParam Long parentAccountId) {
        return ResponseEntity.ok(accountService.existsByParentAccountId(parentAccountId));
    }

    // Code related methods

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAuthority('VIEW_ACCOUNTS')")
    public ResponseEntity<AccountOutput> getAccountByCode(@PathVariable String code) {
        return ResponseEntity.ok(accountService.getAccountDtoByCode(code));
    }

    @GetMapping("/exists/code")
    @PreAuthorize("hasAuthority('VIEW_ACCOUNTS')")
    public ResponseEntity<Boolean> existsByCode(@RequestParam String code) {
        return ResponseEntity.ok(accountService.existsByCode(code));
    }

    @GetMapping("/code/{code}/parent-account")
    @PreAuthorize("hasAuthority('VIEW_ACCOUNTS')")
    public ResponseEntity<AccountOutput> getAccountByCodeWithParentAccount(@PathVariable String code) {
        return ResponseEntity.ok(accountService.getAccountDtoByCodeWithParentAccount(code));
    }

    // Type related methods

    @GetMapping("/type")
    @PreAuthorize("hasAuthority('VIEW_ACCOUNTS')")
    public ResponseEntity<List<AccountOutput>> getAccountsByType(@RequestParam AccountType type) {
        return ResponseEntity.ok(accountService.getAccountDtosByType(type));
    }

    // Name related methods

    @GetMapping("/exists/name")
    @PreAuthorize("hasAuthority('VIEW_ACCOUNTS')")
    public ResponseEntity<Boolean> existsByName(@RequestParam String name) {
        return ResponseEntity.ok(accountService.existsByName(name));
    }

    // Other methods

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_ACCOUNTS')")
    public ResponseEntity<List<AccountOutput>> getAccounts(@RequestParam(required = false) String name, @RequestParam(required = false) Boolean receiveBalance, @RequestParam(required = false) Boolean state) {
        return ResponseEntity.ok(
            name != null ? List.of(accountService.getAccountDtoByName(name)) :
            (receiveBalance != null && state != null) ? accountService.getAllAccountDtosByReceiveBalanceAndState(receiveBalance, state) :
            receiveBalance != null ? accountService.getAllAccountDtosByReceiveBalance(receiveBalance) :
            state != null ? accountService.getAllAccountDtosByState(state) :
            accountService.getAllAccountDtos()
        );
    }
}
