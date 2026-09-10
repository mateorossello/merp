package dev.mateorossello.merp.modules.sales.services;

import dev.mateorossello.merp.exceptions.ResourceConflictException;
import dev.mateorossello.merp.exceptions.ResourceNotFoundException;
import dev.mateorossello.merp.modules.accounting.AccountingDefaults;
import dev.mateorossello.merp.modules.accounting.models.JournalEntry;
import dev.mateorossello.merp.modules.accounting.models.JournalEntryLine;
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
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
    private final TransactionItemService transactionItemService;
    private final AccountService accountService;
    private final JournalEntryService journalEntryService;

    // Utility methods
    // These methods are used to generate random codes and journal entries

    private String generateRandomCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "0123456789";
        
        StringBuilder code = new StringBuilder();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < 3; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }

        for (int i = 0; i < 3; i++) {
            code.append(numbers.charAt(random.nextInt(numbers.length())));
        }

        return code.toString();
    }

    //
    // Create methods
    //

    @Transactional
    public ItemOutput createItem(ItemInput itemInput) {
        Item item = itemMapper.toEntity(itemInput);

        String code = generateRandomCode();
        
        int maximumAttempts = 100;
        int attempt = 0;
        
        while (itemRepository.existsByCode(code)) {
            if (++attempt >= maximumAttempts) {
                throw new ResourceConflictException("Unable to generate a unique item code after " + maximumAttempts + " attempts.");
            }

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
    public void registerPurchases(List<ItemPurchaseInput> itemPurchaseInputs) {
        List<Item> items = new ArrayList<>();
        BigDecimal totalIvaAmount = BigDecimal.ZERO;

        List<Long> itemIds = itemPurchaseInputs.stream().map(ItemPurchaseInput::itemId).toList();
        Map<Long, Item> itemMap = getItemsByIds(itemIds).stream().collect(Collectors.toMap(Item::getId, item -> item));

        if (itemMap.size() != itemIds.size()) {
            throw new ResourceNotFoundException("One or more items not found.");
        }

        for (ItemPurchaseInput itemPurchaseInput : itemPurchaseInputs) {
            Item item = itemMap.get(itemPurchaseInput.itemId());

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

        generatePurchaseJournalEntry(totalAmount, totalIvaAmount);

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

    public List<Item> getItemsByIds(List<Long> ids) {
        return itemRepository.findAllById(ids);
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

    private JournalEntry generatePurchaseJournalEntry(BigDecimal totalAmount, BigDecimal totalIvaAmount) {
        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setEntryDate(java.time.LocalDate.now());
        journalEntry.setDescription("Automatic Journal Entry - Item Purchase");

        JournalEntryLine mercaderiasJournalEntryLine = new JournalEntryLine();
        mercaderiasJournalEntryLine.setAccount(accountService.getAccountByCode(AccountingDefaults.MERCADERIAS));
        mercaderiasJournalEntryLine.setAmount(totalAmount);
        mercaderiasJournalEntryLine.setDebit(true);
        journalEntry.addJournalEntryLine(mercaderiasJournalEntryLine);

        if (totalIvaAmount.compareTo(BigDecimal.ZERO) > 0) {
            JournalEntryLine ivaJournalEntryLine = new JournalEntryLine();
            ivaJournalEntryLine.setAccount(accountService.getAccountByCode(AccountingDefaults.IVA_CREDITO_FISCAL));
            ivaJournalEntryLine.setAmount(totalIvaAmount);
            ivaJournalEntryLine.setDebit(true);
            journalEntry.addJournalEntryLine(ivaJournalEntryLine);
        }

        JournalEntryLine proveedorJournalEntryLine = new JournalEntryLine();
        proveedorJournalEntryLine.setAccount(accountService.getAccountByCode(AccountingDefaults.PROVEEDORES));
        proveedorJournalEntryLine.setAmount(totalAmount.add(totalIvaAmount));
        proveedorJournalEntryLine.setDebit(false);
        journalEntry.addJournalEntryLine(proveedorJournalEntryLine);

        return journalEntryService.createJournalEntry(journalEntry);
    }
}
