package dev.mateorossello.merp.modules.sales.services.documents;

import dev.mateorossello.merp.exceptions.ResourceConflictException;
import dev.mateorossello.merp.exceptions.ResourceNotFoundException;
import dev.mateorossello.merp.modules.sales.dtos.documents.DeliveryNoteInput;
import dev.mateorossello.merp.modules.sales.dtos.documents.DeliveryNoteItemInput;
import dev.mateorossello.merp.modules.sales.dtos.documents.DeliveryNoteOutput;
import dev.mateorossello.merp.modules.sales.mappers.documents.DeliveryNoteMapper;
import dev.mateorossello.merp.modules.sales.models.documents.DeliveryNote;
import dev.mateorossello.merp.modules.sales.models.documents.DeliveryNoteItem;
import dev.mateorossello.merp.modules.sales.models.documents.DeliveryNoteType;
import dev.mateorossello.merp.modules.sales.models.documents.Invoice;
import dev.mateorossello.merp.modules.sales.models.Item;
import dev.mateorossello.merp.modules.sales.models.Transaction;
import dev.mateorossello.merp.modules.sales.repositories.documents.DeliveryNoteRepository;
import dev.mateorossello.merp.modules.sales.repositories.documents.InvoiceRepository;
import dev.mateorossello.merp.modules.sales.services.ItemService;
import dev.mateorossello.merp.modules.sales.services.TransactionService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing DeliveryNote entities. Provides methods for creating, updating, and retrieving delivery notes.
 */

@Service
@AllArgsConstructor
public class DeliveryNoteService {
    private final DeliveryNoteRepository deliveryNoteRepository;
    private final DeliveryNoteMapper deliveryNoteMapper;
    private final ItemService itemService;
    private final TransactionService transactionService;
    private final InvoiceRepository invoiceRepository;

    //
    // Create methods
    //

    @Transactional
    public DeliveryNoteOutput createDeliveryNote(Long userId, DeliveryNoteInput deliveryNoteInput) {
        DeliveryNote deliveryNote = deliveryNoteMapper.toEntity(deliveryNoteInput);
        Transaction transaction = transactionService.getTransactionById(deliveryNoteInput.transactionId());
        
        if (deliveryNoteInput.issueDate().isBefore(transaction.getIssueDate())) {
            throw new ResourceConflictException("Delivery note issue date cannot be before transaction issue date.");
        }

        if (deliveryNoteInput.deliveryDate().isBefore(deliveryNoteInput.issueDate())) {
            throw new ResourceConflictException("Delivery note delivery date cannot be before issue date.");
        }

        List<Invoice> invoices = invoiceRepository.findAllByTransactionId(transaction.getId());
        if (invoices != null && !invoices.isEmpty()) {
            throw new ResourceConflictException("Cannot generate delivery note for a transaction that has already been invoiced.");
        }

        List<DeliveryNote> deliveryNotes = deliveryNoteRepository.findAllByTransactionId(transaction.getId());
        if (deliveryNotes != null && !deliveryNotes.isEmpty()) {
            for (DeliveryNote previousDeliveryNote : deliveryNotes) {
                if (deliveryNoteInput.deliveryDate().isBefore(previousDeliveryNote.getDeliveryDate()) && !previousDeliveryNote.isCancelled()) {
                    throw new ResourceConflictException("Delivery note delivery date cannot be before a previously generated delivery note: " + previousDeliveryNote.getNumber() + ".");
                }
            }
        }

        Map<Long, BigDecimal> previouslySentQuantities = new HashMap<>();
        if (deliveryNotes != null) {
            for (DeliveryNote previousDeliveryNote : deliveryNotes) {
                if (!previousDeliveryNote.isCancelled() && previousDeliveryNote.getDeliveryNoteItems() != null) {
                    for (DeliveryNoteItem deliveryNoteItem : previousDeliveryNote.getDeliveryNoteItems()) {
                        previouslySentQuantities.merge(deliveryNoteItem.getItem().getId(), deliveryNoteItem.getQuantity(), BigDecimal::add);
                    }
                }
            }
        }

        Map<Long, BigDecimal> transactionQuantities = transaction.getTransactionItems().stream()
            .collect(Collectors.toMap(item -> item.getItem().getId(), item -> item.getQuantity(), BigDecimal::add));

        for (DeliveryNoteItemInput deliveryNoteItemInput : deliveryNoteInput.deliveryNoteItemsInput()) {
            Long itemId = deliveryNoteItemInput.itemId();
            BigDecimal requestedQuantity = deliveryNoteItemInput.quantity();
            BigDecimal sentQuantity = previouslySentQuantities.getOrDefault(itemId, BigDecimal.ZERO);
            BigDecimal total = requestedQuantity.add(sentQuantity);
            
            BigDecimal transactionQuantity = transactionQuantities.getOrDefault(itemId, BigDecimal.ZERO);

            if (total.compareTo(transactionQuantity) > 0) {
                throw new ResourceConflictException("Total quantity of items in delivery notes exceeds transaction items quantity.");
            }
        }

        deliveryNote.setNumber(deliveryNoteRepository.findMaxNumber() + 1);
        deliveryNote.setTransaction(transaction);
        deliveryNote.setCreatedByUserId(userId);

        List<Long> itemIds = deliveryNoteInput.deliveryNoteItemsInput().stream().map(DeliveryNoteItemInput::itemId).toList();
        Map<Long, Item> itemMap = itemService.getItemsByIds(itemIds).stream().collect(Collectors.toMap(Item::getId, item -> item));

        if (itemMap.size() != itemIds.size()) {
            throw new ResourceNotFoundException("One or more items not found.");
        }

        for (DeliveryNoteItemInput itemInput : deliveryNoteInput.deliveryNoteItemsInput()) {
            DeliveryNoteItem deliveryNoteItem = new DeliveryNoteItem();
            deliveryNoteItem.setItem(itemMap.get(itemInput.itemId()));
            deliveryNoteItem.setQuantity(itemInput.quantity());
            deliveryNote.addDeliveryItem(deliveryNoteItem);
        }

        return deliveryNoteMapper.toOutput(deliveryNoteRepository.save(deliveryNote));
    }

    //
    // Update methods
    //

    @Transactional
    public DeliveryNoteOutput cancelDeliveryNote(Long id, String reason) {
        DeliveryNote deliveryNote = getDeliveryNoteById(id);

        if (deliveryNote.isCancelled()) {
            throw new ResourceConflictException("Delivery note is already cancelled.");
        }

        if (deliveryNote.getDeliveryDate().isBefore(LocalDate.now())) {
            throw new ResourceConflictException("Cannot cancel a delivery note that has already been delivered.");
        }

        deliveryNote.setCancelled(true);
        deliveryNote.setCancellationReason(reason);

        deliveryNote = deliveryNoteRepository.save(deliveryNote);

        return deliveryNoteMapper.toOutput(deliveryNote);
    }

    //
    // Get methods
    //

    protected DeliveryNote getDeliveryNoteById(Long id) {
        return deliveryNoteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Delivery note not found."));
    }

    public DeliveryNoteOutput getDeliveryNoteDtoById(Long id) {
        return deliveryNoteMapper.toOutput(getDeliveryNoteById(id));
    }

    public List<DeliveryNoteOutput> getAllDeliveryNotes() {
        return deliveryNoteMapper.toOutputList(deliveryNoteRepository.findAll());
    }

    public DeliveryNoteOutput getDeliveryNoteByNumber(Long number) {
        return deliveryNoteRepository.findByNumber(number)
            .map(deliveryNoteMapper::toOutput)
            .orElseThrow(() -> new ResourceNotFoundException("Delivery note not found with number: " + number + "."));
    } 

    public List<DeliveryNoteOutput> getDeliveryNotesByIssueDate(LocalDate issueDate) {
        return deliveryNoteMapper.toOutputList(deliveryNoteRepository.findAllByIssueDate(issueDate));
    }

    public List<DeliveryNote> getDeliveryNotesByTransactionId(Long transactionId) {
        return deliveryNoteRepository.findAllByTransactionId(transactionId);
    }

    public List<DeliveryNoteOutput> getDeliveryNoteDtosByTransactionId(Long transactionId) {
        return deliveryNoteMapper.toOutputList(getDeliveryNotesByTransactionId(transactionId));
    }

    public List<DeliveryNoteOutput> getDeliveryNotesByCreatedByUserId(Long userId) {
        return deliveryNoteMapper.toOutputList(deliveryNoteRepository.findAllByCreatedByUserId(userId));
    }

    public List<DeliveryNoteOutput> getDeliveryNotesByDeliveryDate(LocalDate deliveryDate) {
        return deliveryNoteMapper.toOutputList(deliveryNoteRepository.findAllByDeliveryDate(deliveryDate));
    }

    public List<DeliveryNoteOutput> getDeliveryNotesByCancelled(boolean cancelled) {
        return deliveryNoteMapper.toOutputList(deliveryNoteRepository.findAllByCancelled(cancelled));
    }

    public List<DeliveryNoteOutput> getDeliveryNotesByDeliveryNoteType(DeliveryNoteType deliveryNoteType) {
        return deliveryNoteMapper.toOutputList(deliveryNoteRepository.findAllByDeliveryNoteType(deliveryNoteType));
    }
}
