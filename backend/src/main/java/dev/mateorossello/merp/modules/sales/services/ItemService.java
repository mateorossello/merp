package dev.mateorossello.merp.modules.sales.services;

import dev.mateorossello.merp.exceptions.ResourceNotFoundException;
import dev.mateorossello.merp.modules.accounting.dtos.JournalEntryInput;
import dev.mateorossello.merp.modules.accounting.dtos.JournalEntryLineInput;
import dev.mateorossello.merp.modules.accounting.services.AccountService;
import dev.mateorossello.merp.modules.accounting.services.JournalEntryService;
import dev.mateorossello.merp.modules.sales.dtos.ItemInput;
import dev.mateorossello.merp.modules.sales.dtos.ItemOutput;
import dev.mateorossello.merp.modules.sales.dtos.ItemPurchaseInput;
import dev.mateorossello.merp.modules.sales.mappers.ItemMapper;
import dev.mateorossello.merp.modules.sales.models.Item;
import dev.mateorossello.merp.modules.sales.repositories.ItemRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing Item entities. Provides methods for creating, deleting, updating, and retrieving items.
 */

@Service
@AllArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final AccountService accountService;
    private final JournalEntryService journalEntryService;
    private final TransactionItemService transactionItemService;

    // Utility methods
    // These methods are used to generate random codes and journal entries

    private String generateRandomCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "0123456789";
        
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 3; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }

        for (int i = 0; i < 3; i++) {
            code.append(numbers.charAt(random.nextInt(numbers.length())));
        }

        return code.toString();
    }

    private void generatePurchaseJournalEntry(Long userId, BigDecimal totalAmount, BigDecimal totalIvaAmount) {
        BigDecimal total = totalAmount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal iva = totalIvaAmount.setScale(2, RoundingMode.HALF_UP);

        List<JournalEntryLineInput> lines = new ArrayList<>();

        lines.add(buildLine("Mercaderías", total, true));
        
        if (iva.compareTo(BigDecimal.ZERO) > 0) {
            lines.add(buildLine("IVA crédito fiscal", iva, true));
        }

        lines.add(buildLine("Proveedores", total.add(iva), false));

        JournalEntryInput entry = new JournalEntryInput(
            LocalDate.now(),
            "Compra de mercaderías",
            lines
        );

        journalEntryService.createJournalEntry(entry, userId);
    }

    private JournalEntryLineInput buildLine(String accountName, BigDecimal amount, boolean debit) {
        return new JournalEntryLineInput(
            accountService.getAccountByName(accountName).getId(),
            amount,
            debit,
            null
        );
    }

    //
    // Create methods
    //

    @Transactional
    public ItemOutput createItem(ItemInput itemInput) {
        Item item = itemMapper.toEntity(itemInput);

        String code = generateRandomCode();
        while (itemRepository.existsByCode(code)) {
            code = generateRandomCode();
        }

        item.setCode(code);
        
        item = itemRepository.save(item);

        return itemMapper.toOutput(item);
    }

    //
    // Delete methods
    //

    @Transactional
    public void deleteItem(Long id) {
        Item item = getItemById(id);

        if (transactionItemService.existsTransactionItemByItemId(item.getId())) {
            item.setAvailable(false);
            itemRepository.save(item);
        } else {
            itemRepository.delete(item);
        }
    }

    //
    // Update methods
    //

    @Transactional
    public ItemOutput updateItem(Long id, ItemInput itemInput) {
        Item item = getItemById(id);

        item.setName(itemInput.name());
        item.setDescription(itemInput.description());
        item.setAvailable(itemInput.available());
        item.setMinimumStock(itemInput.minimumStock());
        item.setIva(itemInput.iva());

        item = itemRepository.save(item);

        return itemMapper.toOutput(item);
    }

    @Transactional
    public void registerPurchases(Long userId, List<ItemPurchaseInput> itemPurchaseInputs) {
        List<Item> items = new ArrayList<>();
        BigDecimal totalIvaAmount = BigDecimal.ZERO;

        for (ItemPurchaseInput itemPurchaseInput : itemPurchaseInputs) {
            Item item = getItemById(itemPurchaseInput.itemId());

            BigDecimal currentStock = item.getCurrentStock();
            BigDecimal purchaseQuantity = itemPurchaseInput.quantity();

            BigDecimal newUnitPrice = (item.getUnitPrice().multiply(currentStock))
                .add(itemPurchaseInput.purchaseUnitPrice().multiply(purchaseQuantity))
                .divide(currentStock.add(purchaseQuantity), 2, RoundingMode.HALF_UP);

            item.setUnitPrice(newUnitPrice);
            item.setCurrentStock(currentStock.add(purchaseQuantity));

            totalIvaAmount = totalIvaAmount.add(
                itemPurchaseInput.purchaseUnitPrice()
                    .multiply(purchaseQuantity)
                    .multiply(item.getIva())
                    .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)
            );

            items.add(item);
        }

        BigDecimal totalAmount = itemPurchaseInputs.stream()
            .map(purchase -> purchase.purchaseUnitPrice().multiply(purchase.quantity()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        generatePurchaseJournalEntry(userId, totalAmount, totalIvaAmount);

        itemRepository.saveAll(items);
    }

    //
    // Get methods
    //

    public Item getItemById(long id) {
        return itemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Item not found."));
    }

    public ItemOutput getItemDtoById(long id) {
        return itemMapper.toOutput(getItemById(id));
    }

    public List<ItemOutput> getAllItems() {
        return itemMapper.toOutputList(itemRepository.findAll());
    }

    public ItemOutput getItemByCode(String code) {
        return itemRepository.findByCode(code)
            .map(itemMapper::toOutput)
            .orElseThrow(() -> new ResourceNotFoundException("Item not found with code: " + code + "."));
    }

    public List<ItemOutput> getItemsByAvailable(boolean available) {
        return itemMapper.toOutputList(itemRepository.findAllByAvailable(available));
    }

    public List<ItemOutput> getItemsBelowMinimumStock() {
        return itemMapper.toOutputList(itemRepository.findAllByCurrentStockLessThanMinimumStock());
    }
}
