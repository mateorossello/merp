package dev.mateorossello.merp.modules.sales.services.documents;

import dev.mateorossello.merp.exceptions.ResourceConflictException;
import dev.mateorossello.merp.exceptions.ResourceNotFoundException;
import dev.mateorossello.merp.modules.accounting.AccountingDefaults;
import dev.mateorossello.merp.modules.accounting.models.JournalEntry;
import dev.mateorossello.merp.modules.accounting.models.JournalEntryLine;
import dev.mateorossello.merp.modules.accounting.services.AccountService;
import dev.mateorossello.merp.modules.accounting.services.JournalEntryService;
import dev.mateorossello.merp.modules.sales.dtos.documents.InvoiceInput;
import dev.mateorossello.merp.modules.sales.dtos.documents.InvoiceOutput;
import dev.mateorossello.merp.modules.sales.mappers.documents.InvoiceMapper;
import dev.mateorossello.merp.modules.sales.models.Customer;
import dev.mateorossello.merp.modules.sales.models.documents.DeliveryNote;
import dev.mateorossello.merp.modules.sales.models.documents.DeliveryNoteItem;
import dev.mateorossello.merp.modules.sales.models.documents.Invoice;
import dev.mateorossello.merp.modules.sales.models.documents.InvoiceType;
import dev.mateorossello.merp.modules.sales.models.FiscalType;
import dev.mateorossello.merp.modules.sales.models.Transaction;
import dev.mateorossello.merp.modules.sales.models.TransactionItem;
import dev.mateorossello.merp.modules.sales.repositories.documents.DeliveryNoteRepository;
import dev.mateorossello.merp.modules.sales.repositories.documents.InvoiceRepository;
import dev.mateorossello.merp.modules.sales.services.FiscalConfigurationService;
import dev.mateorossello.merp.modules.sales.services.TransactionService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing Invoice entities. Provides methods for creating and retrieving invoices.
 */

@Service
@AllArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final FiscalConfigurationService fiscalConfigurationService;
    private final TransactionService transactionService;
    private final DeliveryNoteRepository deliveryNoteRepository;
    private final AccountService accountService;
    private final JournalEntryService journalEntryService;

    //
    // Create methods
    //

    @Transactional
    public InvoiceOutput createInvoice(InvoiceInput invoiceInput) {
        Invoice invoice = invoiceMapper.toEntity(invoiceInput);
        Transaction transaction = transactionService.getTransactionById(invoiceInput.transactionId());

        List<DeliveryNote> deliveryNotes = deliveryNoteRepository.findAllByTransactionId(transaction.getId());

        if (deliveryNotes != null && !deliveryNotes.isEmpty()) {
            Map<Long, BigDecimal> transactionItemsQuantity = transaction.getTransactionItems().stream()
                .collect(Collectors.toMap(transactionItem -> transactionItem.getItem().getId(), TransactionItem::getQuantity, BigDecimal::add));

            Map<Long, BigDecimal> deliveryItemsQuantity = deliveryNotes.stream()
                .flatMap(deliveryNote -> deliveryNote.getDeliveryNoteItems().stream())
                .collect(Collectors.toMap(item -> item.getItem().getId(), DeliveryNoteItem::getQuantity, BigDecimal::add));
            
            for (Entry<Long, BigDecimal> entry : transactionItemsQuantity.entrySet()) {
                Long articleId = entry.getKey();
                BigDecimal transactionQuantity = entry.getValue();
                BigDecimal deliveryQuantity = deliveryItemsQuantity.getOrDefault(articleId, BigDecimal.ZERO);

                if (transactionQuantity.compareTo(deliveryQuantity) > 0) {
                    throw new ResourceConflictException("Cannot create invoice. Not all items from the transaction have been sent via delivery notes.");
                }
            }

            for (DeliveryNote deliveryNote : deliveryNotes) {
                if (invoiceInput.issueDate().isBefore(deliveryNote.getDeliveryDate())) {
                    throw new ResourceConflictException("Invoice issue date cannot be before delivery note " + deliveryNote.getNumber() + " delivery date.");
                }
            }
        }

        if (invoiceInput.issueDate().isBefore(transaction.getIssueDate())) {
            throw new ResourceConflictException("Invoice issue date cannot be before transaction issue date.");
        }

        if (invoiceInput.issueDate().isAfter(LocalDate.now())) {
            throw new ResourceConflictException("Invoice issue date cannot be in the future.");
        }

        long daysDiff = ChronoUnit.DAYS.between(invoiceInput.issueDate(), LocalDate.now());

        if (daysDiff > 30) {
            throw new ResourceConflictException("Invoice issue date cannot be older than 30 days.");
        }

        if (invoiceInput.dueDate().isBefore(invoiceInput.issueDate())) {
            throw new ResourceConflictException("Invoice due date cannot be before issue date.");
        }

        invoice.setNumber(invoiceRepository.findMaxNumber() + 1);
        invoice.setTransaction(transaction);

        Customer customer = transaction.getCustomer();

        if (fiscalConfigurationService.getFiscalConfiguration().getFiscalType() == FiscalType.RESPONSABLE_INSCRIPTO) {
            if (customer.getFiscalType() == FiscalType.RESPONSABLE_INSCRIPTO) {
                invoice.setInvoiceType(InvoiceType.A);
            } else {
                invoice.setInvoiceType(InvoiceType.B);
            }
        } else {
            invoice.setInvoiceType(InvoiceType.C);
        }

        BigDecimal totalSale = BigDecimal.ZERO;
        BigDecimal totalSaleWithIva = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;

        for (TransactionItem transactionItem : transaction.getTransactionItems()) {
            BigDecimal baseTotal = transactionItem.getQuantity().multiply(transactionItem.getUnitCost());
            BigDecimal discount = baseTotal.multiply(transactionItem.getDiscount()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            BigDecimal subtotal = baseTotal.subtract(discount);
            BigDecimal iva = subtotal.multiply(transactionItem.getItem().getIva()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            
            totalSale = totalSale.add(subtotal);
            totalSaleWithIva = totalSaleWithIva.add(subtotal.add(iva));
            totalCost = totalCost.add(transactionItem.getQuantity().multiply(transactionItem.getUnitCost()));
        }

        JournalEntry journalEntry = generateInvoiceJournalEntry(invoice, invoice.getInvoiceType(), totalSale, totalSaleWithIva, totalCost);
        invoice.setJournalEntry(journalEntry);
        
        invoice = invoiceRepository.save(invoice);

        return invoiceMapper.toOutput(invoice);
    }

    //
    // Get methods
    //

    protected Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice not found."));
    }

    public InvoiceOutput getInvoiceDtoById(Long id) {
        return invoiceMapper.toOutput(getInvoiceById(id));
    }

    public List<InvoiceOutput> getAllInvoices() {
        return invoiceMapper.toOutputList(invoiceRepository.findAll());
    }

    public InvoiceOutput getInvoiceByNumber(Long number) {
        return invoiceRepository.findByNumber(number)
            .map(invoiceMapper::toOutput)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with number: " + number + "."));
    }

    public List<InvoiceOutput> getInvoicesByIssueDate(LocalDate issueDate) {
        return invoiceMapper.toOutputList(invoiceRepository.findAllByIssueDate(issueDate));
    }

    public List<Invoice> getInvoicesByTransactionId(Long transactionId) {
        return invoiceRepository.findAllByTransactionId(transactionId);
    }

    public List<InvoiceOutput> getInvoiceDtosByTransactionId(Long transactionId) {
        return invoiceMapper.toOutputList(getInvoicesByTransactionId(transactionId));
    }

    public List<InvoiceOutput> getInvoicesByCreatedByUserId(Long userId) {
        return invoiceMapper.toOutputList(invoiceRepository.findAllByCreatedByUserId(userId));
    }

    public List<InvoiceOutput> getInvoicesByDueDate(LocalDate dueDate) {
        return invoiceMapper.toOutputList(invoiceRepository.findAllByDueDate(dueDate));
    }

    private JournalEntry generateInvoiceJournalEntry(Invoice invoice, InvoiceType invoiceType, BigDecimal totalSale, BigDecimal totalSaleWithIva, BigDecimal totalCost) {
        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setEntryDate(invoice.getIssueDate());
        journalEntry.setDescription("Automatic Journal Entry - Invoice #" + invoice.getNumber());

        // Deudores por Ventas
        JournalEntryLine deudoresJournalEntryLine = new JournalEntryLine();
        deudoresJournalEntryLine.setAccount(accountService.getAccountByCode(AccountingDefaults.DEUDORES_POR_VENTAS));
        deudoresJournalEntryLine.setAmount(totalSaleWithIva);
        deudoresJournalEntryLine.setDebit(true);
        journalEntry.addJournalEntryLine(deudoresJournalEntryLine);

        // Ventas
        JournalEntryLine ventasJournalEntryLine = new JournalEntryLine();
        ventasJournalEntryLine.setAccount(accountService.getAccountByCode(AccountingDefaults.VENTAS));
        ventasJournalEntryLine.setAmount(invoiceType == InvoiceType.C ? totalSaleWithIva : totalSale);
        ventasJournalEntryLine.setDebit(false);
        journalEntry.addJournalEntryLine(ventasJournalEntryLine);

        if (invoiceType != InvoiceType.C && totalSaleWithIva.compareTo(totalSale) > 0) {
            // IVA Débito Fiscal
            JournalEntryLine ivaJournalEntryLine = new JournalEntryLine();
            ivaJournalEntryLine.setAccount(accountService.getAccountByCode(AccountingDefaults.IVA_DEBITO_FISCAL));
            ivaJournalEntryLine.setAmount(totalSaleWithIva.subtract(totalSale));
            ivaJournalEntryLine.setDebit(false);
            journalEntry.addJournalEntryLine(ivaJournalEntryLine);
        }

        if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
            // CMV
            JournalEntryLine cmvJournalEntryLine = new JournalEntryLine();
            cmvJournalEntryLine.setAccount(accountService.getAccountByCode(AccountingDefaults.CMV));
            cmvJournalEntryLine.setAmount(totalCost);
            cmvJournalEntryLine.setDebit(true);
            journalEntry.addJournalEntryLine(cmvJournalEntryLine);

            // Mercaderías
            JournalEntryLine mercaderiasJournalEntryLine = new JournalEntryLine();
            mercaderiasJournalEntryLine.setAccount(accountService.getAccountByCode(AccountingDefaults.MERCADERIAS));
            mercaderiasJournalEntryLine.setAmount(totalCost);
            mercaderiasJournalEntryLine.setDebit(false);
            journalEntry.addJournalEntryLine(mercaderiasJournalEntryLine);
        }

        return journalEntryService.createJournalEntry(journalEntry);
    }
}
