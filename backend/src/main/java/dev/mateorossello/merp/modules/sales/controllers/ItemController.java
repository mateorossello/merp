package dev.mateorossello.merp.modules.sales.controllers;

import dev.mateorossello.merp.modules.sales.dtos.ItemInput;
import dev.mateorossello.merp.modules.sales.dtos.ItemOutput;
import dev.mateorossello.merp.modules.sales.dtos.ItemPurchaseInput;
import dev.mateorossello.merp.modules.sales.services.ItemService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for managing Item entities.
 */
@RestController
@RequestMapping("/sales/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;

    //
    // Create methods
    //

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGE_ITEMS')")
    public ResponseEntity<ItemOutput> createItem(@Valid @RequestBody ItemInput itemInput) {
        return ResponseEntity.ok(itemService.createItem(itemInput));
    }

    //
    // Delete methods
    //
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGE_ITEMS')")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    //
    // Update methods
    //

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGE_ITEMS')")
    public ResponseEntity<ItemOutput> updateItem(@PathVariable Long id, @Valid @RequestBody ItemInput itemInput) {
        return ResponseEntity.ok(itemService.updateItem(id, itemInput));
    }

    @PostMapping("/purchases")
    @PreAuthorize("hasAuthority('MANAGE_ITEMS')")
    public ResponseEntity<Void> registerPurchases(@Valid @RequestBody List<ItemPurchaseInput> itemPurchases) {
        itemService.registerPurchases(itemPurchases);
        return ResponseEntity.ok().build();
    }

    //
    // Get methods
    //

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_ITEMS')")
    public ResponseEntity<ItemOutput> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItemDtoById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_ITEMS')")
    public ResponseEntity<List<ItemOutput>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAuthority('VIEW_ITEMS')")
    public ResponseEntity<ItemOutput> getItemByCode(@PathVariable String code) {
        return ResponseEntity.ok(itemService.getItemByCode(code));
    }

    @GetMapping("/available")
    @PreAuthorize("hasAuthority('VIEW_ITEMS')")
    public ResponseEntity<List<ItemOutput>> getItemsByAvailable(@RequestParam Boolean available) {
        return ResponseEntity.ok(itemService.getItemsByAvailable(available));
    }

    @GetMapping("/below-minimum-stock")
    @PreAuthorize("hasAuthority('VIEW_ITEMS')")
    public ResponseEntity<List<ItemOutput>> getItemsBelowMinimumStock() {
        return ResponseEntity.ok(itemService.getItemsBelowMinimumStock());
    }
}
