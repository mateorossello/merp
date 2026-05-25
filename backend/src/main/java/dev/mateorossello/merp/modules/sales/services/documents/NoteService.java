package dev.mateorossello.merp.modules.sales.services.documents;

import dev.mateorossello.merp.exceptions.ResourceConflictException;
import dev.mateorossello.merp.exceptions.ResourceNotFoundException;
import dev.mateorossello.merp.modules.sales.dtos.documents.NoteAdjustmentInput;
import dev.mateorossello.merp.modules.sales.dtos.documents.NoteInput;
import dev.mateorossello.merp.modules.sales.dtos.documents.NoteItemInput;
import dev.mateorossello.merp.modules.sales.dtos.documents.NoteOutput;
import dev.mateorossello.merp.modules.sales.mappers.documents.NoteMapper;
import dev.mateorossello.merp.modules.sales.models.TransactionItem;
import dev.mateorossello.merp.modules.sales.models.documents.Invoice;
import dev.mateorossello.merp.modules.sales.models.documents.Note;
import dev.mateorossello.merp.modules.sales.models.documents.NoteAdjustment;
import dev.mateorossello.merp.modules.sales.models.documents.NoteItem;
import dev.mateorossello.merp.modules.sales.models.documents.NoteType;
import dev.mateorossello.merp.modules.sales.repositories.documents.NoteRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing Note entities. Provides methods for creating and deleting notes.
 */

@Service
@AllArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;
    private final InvoiceService invoiceService;

    //
    // Create methods
    //

    @Transactional
    public NoteOutput createNote(Long userId, NoteInput noteInput) {
        if (noteInput.noteItemsInput() != null) {
            for (NoteItemInput noteItemInput : noteInput.noteItemsInput()) {
                if ((noteItemInput.itemId() == null || noteItemInput.itemId() == 0) && noteItemInput.quantity().equals(BigDecimal.ZERO)) {
                    continue;
                }

                if ((noteItemInput.itemId() == null || noteItemInput.itemId() == 0) && !noteItemInput.quantity().equals(BigDecimal.ZERO)) {
                    throw new ResourceConflictException("Invalid item ID.");
                }

                if (noteItemInput.itemId() != null && noteItemInput.itemId() != 0 && noteItemInput.quantity().equals(BigDecimal.ZERO)) {
                    throw new ResourceConflictException("Invalid quantity for item.");
                }
            }
        }

        if (noteInput.noteAdjustmentsInput() != null) {
            for (NoteAdjustmentInput noteAdjustmentInput : noteInput.noteAdjustmentsInput()) {
                if ((noteAdjustmentInput.description() == null || noteAdjustmentInput.description().isBlank()) && (noteAdjustmentInput.amount() == null || noteAdjustmentInput.amount().compareTo(BigDecimal.ZERO) == 0)) {
                    continue;
                }

                if ((noteAdjustmentInput.description() == null || noteAdjustmentInput.description().isBlank()) && noteAdjustmentInput.amount().compareTo(BigDecimal.ZERO) != 0) {
                    throw new ResourceConflictException("Invalid adjustment description.");
                }

                if (noteAdjustmentInput.description() != null && !noteAdjustmentInput.description().isBlank() && noteAdjustmentInput.amount().compareTo(BigDecimal.ZERO) == 0) {
                    throw new ResourceConflictException("Invalid amount for adjustment.");
                }
            }
        }

        Note note = noteMapper.toEntity(noteInput);
        Invoice invoice = invoiceService.getInvoiceById(noteInput.invoiceId());

        if (noteInput.issueDate().isBefore(invoice.getIssueDate())) {
            throw new ResourceConflictException("Note issue date cannot be before invoice issue date.");
        }

        if (note.getNoteType() == null || (!note.getNoteType().equals(NoteType.DEBIT) && !note.getNoteType().equals(NoteType.CREDIT))) {
            throw new ResourceConflictException("Note type must be DEBIT or CREDIT.");
        }

        if ((note.getNoteItems() == null || note.getNoteItems().isEmpty()) && (note.getNoteAdjustments() == null || note.getNoteAdjustments().isEmpty())) {
            throw new ResourceConflictException("Note must contain at least one item or adjustment");
        }

        if (note.getNoteItems() != null && note.getNoteItems().stream().map(NoteItem::getItemId).distinct().count() != note.getNoteItems().size()) {
            throw new ResourceConflictException("Note contains duplicate items.");
        }

        if (note.getNoteAdjustments() != null && note.getNoteAdjustments().stream().map(NoteAdjustment::getDescription).distinct().count() != note.getNoteAdjustments().size()) {
            throw new ResourceConflictException("Note contains duplicate adjustments");
        }

        if (note.getNoteType() == NoteType.CREDIT && note.getNoteItems() != null) {
            for (NoteItem noteItem : note.getNoteItems()) {
                Long itemId = noteItem.getItemId();
                BigDecimal totalNoteQuantity = noteItem.getQuantity();

                List<Note> invoiceNotes = noteRepository.findAllByInvoiceId(invoice.getId());
                if (invoiceNotes != null) {
                    for (Note invoiceNote : invoiceNotes) {
                        if (invoiceNote.getNoteItems() != null) {
                            totalNoteQuantity = totalNoteQuantity.add(invoiceNote.getNoteItems().stream()
                                .filter(item -> item.getItemId().equals(itemId))
                                .map(NoteItem::getQuantity)
                                .reduce(BigDecimal.ZERO, BigDecimal::add));
                        }
                    }
                }

                BigDecimal transactionQuantity = invoice.getTransaction().getTransactionItems().stream()
                    .filter(item -> item.getItem().getId().equals(itemId))
                    .map(TransactionItem::getQuantity)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (totalNoteQuantity.compareTo(transactionQuantity) > 0) {
                    throw new ResourceConflictException("Total quantity of items in credit notes exceeds transaction items quantity.");
                }
            }
        }

        note.setInvoice(invoice);
        note.setCreatedByUserId(userId);
        note.setNumber(noteRepository.findMaxNumber() + 1);

        if (noteInput.noteItemsInput() != null) {
            for (NoteItemInput itemInput : noteInput.noteItemsInput()) {
                if ((itemInput.itemId() == null || itemInput.itemId() == 0) && itemInput.quantity().equals(BigDecimal.ZERO)) continue;
                
                NoteItem noteItem = new NoteItem();
                noteItem.setItemId(itemInput.itemId());
                noteItem.setQuantity(itemInput.quantity());
                note.addNoteItem(noteItem);
            }
        }
        
        if (noteInput.noteAdjustmentsInput() != null) {
            for (NoteAdjustmentInput adjInput : noteInput.noteAdjustmentsInput()) {
                if ((adjInput.description() == null || adjInput.description().isBlank()) && (adjInput.amount() == null || adjInput.amount().compareTo(BigDecimal.ZERO) == 0)) continue;
                
                NoteAdjustment adjustment = new NoteAdjustment();
                adjustment.setDescription(adjInput.description());
                adjustment.setAmount(adjInput.amount());
                note.addAdjustment(adjustment);
            }
        }

        BigDecimal totalItems = BigDecimal.ZERO;
        BigDecimal totalIva = BigDecimal.ZERO;

        BigDecimal totalAdjustments = note.getNoteAdjustments() != null ? note.getNoteAdjustments().stream()
            .map(NoteAdjustment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO;
            
        BigDecimal totalCost = BigDecimal.ZERO;

        if (note.getNoteItems() != null && !note.getNoteItems().isEmpty()) {
            totalCost = note.getNoteItems().stream()
                .map(item -> item.getQuantity().multiply(
                    invoice.getTransaction().getTransactionItems().stream()
                        .filter(transactionItem -> transactionItem.getItem().getId().equals(item.getItemId()))
                        .map(TransactionItem::getUnitCost)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            for (NoteItem noteItem : note.getNoteItems()) {
                BigDecimal baseTotal = note.getNoteItems().stream()
                    .filter(nI -> nI.getItemId().equals(noteItem.getItemId()))
                    .map(nI -> nI.getQuantity().multiply(
                        invoice.getTransaction().getTransactionItems().stream()
                            .filter(tI -> tI.getItem().getId().equals(nI.getItemId()))
                            .map(TransactionItem::getUnitCost)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                    ))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal discount = baseTotal.multiply(
                    invoice.getTransaction().getTransactionItems().stream()
                        .filter(tI -> tI.getItem().getId().equals(noteItem.getItemId()))
                        .map(TransactionItem::getDiscount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                ).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

                BigDecimal subtotal = baseTotal.subtract(discount);

                BigDecimal iva = subtotal.multiply(
                    invoice.getTransaction().getTransactionItems().stream()
                        .filter(tI -> tI.getItem().getId().equals(noteItem.getItemId()))
                        .map(TransactionItem::getIva)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                ).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

                totalItems = totalItems.add(subtotal);
                totalIva = totalIva.add(iva);
            }
        }

        // generateNoteJournalEntry(userId, note, note.getNoteType(), 
        // note.getNoteItems() != null && !note.getNoteItems().isEmpty(), 
        // note.getNoteAdjustments() != null && !note.getNoteAdjustments().isEmpty(), 
        // totalItems, totalIva, totalAdjustments, totalCost);

        note = noteRepository.save(note);

        return noteMapper.toOutput(note);
    }

    //
    // Get methods
    //

    protected Note getNoteById(Long id) {
        return noteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Note not found."));
    }

    public NoteOutput getNoteDtoById(Long id) {
        return noteMapper.toOutput(getNoteById(id));
    }

    public List<NoteOutput> getAllNotes() {
        return noteMapper.toOutputList(noteRepository.findAll());
    }

    public NoteOutput getNoteByNumber(Long number) {
        return noteRepository.findByNumber(number)
            .map(noteMapper::toOutput)
            .orElseThrow(() -> new ResourceNotFoundException("Note not found with number: " + number + "."));
    }

    public List<NoteOutput> getNotesByIssueDate(LocalDate issueDate) {
        return noteMapper.toOutputList(noteRepository.findAllByIssueDate(issueDate));
    }

    public List<NoteOutput> getNotesByTransactionId(Long transactionId) {
        return noteMapper.toOutputList(noteRepository.findAllByTransactionId(transactionId));
    }

    public List<NoteOutput> getNotesByCreatedByUserId(Long userId) {
        return noteMapper.toOutputList(noteRepository.findAllByCreatedByUserId(userId));
    }

    public List<NoteOutput> getNotesByInvoiceId(Long invoiceId) {
        return noteMapper.toOutputList(noteRepository.findAllByInvoiceId(invoiceId));
    }

    public List<NoteOutput> getNotesByNoteType(NoteType noteType) {
        return noteMapper.toOutputList(noteRepository.findAllByNoteType(noteType));
    }

    // TODO: Generar asientos a partir de las notas.
}
