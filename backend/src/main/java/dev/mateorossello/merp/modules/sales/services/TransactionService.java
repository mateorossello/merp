package dev.mateorossello.merp.modules.sales.services;

import dev.mateorossello.merp.exceptions.ResourceConflictException;
import dev.mateorossello.merp.exceptions.ResourceNotFoundException;
import dev.mateorossello.merp.modules.sales.dtos.TransactionInput;
import dev.mateorossello.merp.modules.sales.dtos.TransactionItemInput;
import dev.mateorossello.merp.modules.sales.dtos.TransactionOutput;
import dev.mateorossello.merp.modules.sales.dtos.TransactionPaymentMethodInput;
import dev.mateorossello.merp.modules.sales.mappers.TransactionMapper;
import dev.mateorossello.merp.modules.sales.models.Customer;
import dev.mateorossello.merp.modules.sales.models.documents.DeliveryNote;
import dev.mateorossello.merp.modules.sales.models.documents.Invoice;
import dev.mateorossello.merp.modules.sales.models.FiscalType;
import dev.mateorossello.merp.modules.sales.models.Item;
import dev.mateorossello.merp.modules.sales.models.PaymentMethod;
import dev.mateorossello.merp.modules.sales.models.Transaction;
import dev.mateorossello.merp.modules.sales.models.TransactionItem;
import dev.mateorossello.merp.modules.sales.models.TransactionPaymentMethod;
import dev.mateorossello.merp.modules.sales.models.TransactionType;
import dev.mateorossello.merp.modules.sales.repositories.documents.DeliveryNoteRepository;
import dev.mateorossello.merp.modules.sales.repositories.documents.InvoiceRepository;
import dev.mateorossello.merp.modules.sales.repositories.TransactionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing Transaction entities. Provides methods for creating, cancelling, and retrieving transactions.
 */

@Service
@AllArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final CustomerService customerService;
    private final ItemService itemService;
    private final PaymentMethodService paymentMethodService;
    private final DeliveryNoteRepository deliveryNoteRepository;
    private final InvoiceRepository invoiceRepository;

    //
    // Create methods
    //

    @Transactional
    public TransactionOutput createTransaction(Long userId, TransactionInput transactionInput) {
        Customer customer = customerService.getCustomerById(transactionInput.customerId());
        Transaction transaction = new Transaction();
        transaction.setIssueDate(LocalDate.now());
        transaction.setTransactionType(TransactionType.SALE);

        List<TransactionItem> transactionItems = new ArrayList<>();

        List<Long> itemIds = transactionInput.transactionItemsInput().stream().map(TransactionItemInput::itemId).toList();
        Map<Long, Item> itemMap = itemService.getItemsByIds(itemIds).stream().collect(Collectors.toMap(Item::getId, item -> item));

        if (itemMap.size() != itemIds.size()) {
            throw new ResourceNotFoundException("One or more items not found.");
        }

        for (TransactionItemInput itemInput : transactionInput.transactionItemsInput()) {
            Item item = itemMap.get(itemInput.itemId());

            if (item.getCurrentStock().compareTo(itemInput.quantity()) < 0) {
                throw new ResourceConflictException("Not enough stock for item " + item.getCode() + " - " + item.getName() + ".");
            }
        }

        for (TransactionItemInput itemInput : transactionInput.transactionItemsInput()) {
            Item item = itemMap.get(itemInput.itemId());

            item.setCurrentStock(item.getCurrentStock().subtract(itemInput.quantity()));
            
            TransactionItem transactionItem = new TransactionItem();
            transactionItem.setQuantity(itemInput.quantity());
            transactionItem.setDiscount(itemInput.discount());
            transactionItem.setTransaction(transaction);
            transactionItem.setItem(item);
            transactionItem.setUnitCost(item.getUnitPrice());

            if (customer.getFiscalType() == FiscalType.RESPONSABLE_INSCRIPTO || customer.getFiscalType() == FiscalType.MONOTRIBUTISTA || customer.getFiscalType() == FiscalType.CONSUMIDOR_FINAL) {
                transactionItem.setIva(item.getIva());
            } else if (customer.getFiscalType() == FiscalType.EXENTO) {
                transactionItem.setIva(BigDecimal.ZERO);
            }

            BigDecimal totalPrice = item.getUnitPrice().multiply(transactionItem.getQuantity());
            BigDecimal discountAmount = totalPrice.multiply(transactionItem.getDiscount()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            BigDecimal priceWithDiscount = totalPrice.subtract(discountAmount);
            BigDecimal ivaAmount = priceWithDiscount.multiply(transactionItem.getIva()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            BigDecimal subtotal = priceWithDiscount.add(ivaAmount);
            
            transactionItem.setSubtotal(subtotal);
            transactionItems.add(transactionItem);
        }

        if (transactionItems.isEmpty()) {
            throw new ResourceConflictException("Cannot create transaction without items.");
        }

        List<TransactionPaymentMethod> transactionPaymentMethods = new ArrayList<>();

        List<Long> paymentMethodIds = transactionInput.transactionPaymentMethodsInput().stream().map(TransactionPaymentMethodInput::paymentMethodId).toList();
        Map<Long, PaymentMethod> paymentMethodMap = paymentMethodService.getPaymentMethodsByIds(paymentMethodIds).stream().collect(Collectors.toMap(PaymentMethod::getId, paymentMethod -> paymentMethod));

        if (paymentMethodMap.size() != paymentMethodIds.size()) {
            throw new ResourceNotFoundException("One or more payment methods not found.");
        }

        for (TransactionPaymentMethodInput transactionPaymentMethodInput : transactionInput.transactionPaymentMethodsInput()) {
            PaymentMethod paymentMethod = paymentMethodMap.get(transactionPaymentMethodInput.paymentMethodId());

            TransactionPaymentMethod transactionPaymentMethod = new TransactionPaymentMethod();
            transactionPaymentMethod.setAmount(transactionPaymentMethodInput.amount());
            transactionPaymentMethod.setTransaction(transaction);
            transactionPaymentMethod.setPaymentMethod(paymentMethod);

            transactionPaymentMethods.add(transactionPaymentMethod);
        }

        if (transactionPaymentMethods.isEmpty()) {
            throw new ResourceConflictException("Cannot create transaction without payment methods.");
        }

        BigDecimal itemsTotal = transactionItems.stream().map(TransactionItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal paymentsTotal = transactionPaymentMethods.stream().map(TransactionPaymentMethod::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        if (itemsTotal.compareTo(paymentsTotal) != 0) {
            throw new ResourceConflictException("Cannot create transaction. Total items amount does not match total payment amount.");
        }

        transaction.setCreatedByUserId(userId);
        transaction.setCustomer(customer);
        transaction.setReceiptNumber(transactionRepository.findMaxReceiptNumber() + 1);

        transactionItems.forEach(transaction::addTransactionItem);
        transactionPaymentMethods.forEach(transaction::addTransactionPayment);

        transaction.setTotal(itemsTotal);

        transaction = transactionRepository.save(transaction);

        return transactionMapper.toOutput(transaction);
    }

    //
    // Update methods
    //

    @Transactional
    public TransactionOutput cancelTransaction(Long id) {
        Transaction transaction = getTransactionById(id);

        if (transaction.isCancelled()) {
            throw new ResourceConflictException("Transaction is already cancelled.");
        }

        List<DeliveryNote> deliveryNotes = deliveryNoteRepository.findAllByTransactionId(transaction.getId());
        List<Invoice> invoices = invoiceRepository.findAllByTransactionId(transaction.getId());

        boolean canCancel = (deliveryNotes == null || deliveryNotes.isEmpty()) && (invoices == null || invoices.isEmpty());
        
        if (!canCancel && deliveryNotes != null) {
            canCancel = deliveryNotes.stream().allMatch(deliveryNote -> deliveryNote.isCancelled()) && (invoices == null || invoices.isEmpty());
        }

        if (canCancel) {
            List<Long> itemIds = transaction.getTransactionItems().stream().map(transactionItem -> transactionItem.getItem().getId()).toList();
            Map<Long, Item> itemMap = itemService.getItemsByIds(itemIds).stream().collect(Collectors.toMap(Item::getId, item -> item));

            for (TransactionItem transactionItem : transaction.getTransactionItems()) {
                Item item = itemMap.get(transactionItem.getItem().getId());
                
                BigDecimal newStock = item.getCurrentStock().add(transactionItem.getQuantity());
                
                BigDecimal totalCurrentValue = item.getUnitPrice().multiply(item.getCurrentStock());
                BigDecimal returnedValue = transactionItem.getUnitCost().multiply(transactionItem.getQuantity());
                
                BigDecimal newUnitPrice = totalCurrentValue.add(returnedValue).divide(newStock, 2, RoundingMode.HALF_UP);
                    
                item.setCurrentStock(newStock);
                item.setUnitPrice(newUnitPrice);
            }

            transaction.setCancelled(true);

            transaction = transactionRepository.save(transaction);

            return transactionMapper.toOutput(transaction);
        } else {
            throw new ResourceConflictException("Cannot cancel transaction. Delivery notes have been generated and not cancelled, or an invoice has been generated.");
        }
    }

    //
    // Get methods
    //

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found."));
    }

    public TransactionOutput getTransactionDtoById(Long id) {
        return transactionMapper.toOutput(getTransactionById(id));
    }

    public List<TransactionOutput> getAllTransactions() {
        return transactionMapper.toOutputList(transactionRepository.findAll());
    }

    public List<TransactionOutput> getTransactionsByIssueDate(LocalDate issueDate) {
        return transactionMapper.toOutputList(transactionRepository.findAllByIssueDate(issueDate));
    }

    public List<TransactionOutput> getTransactionsByCreatedByUserId(Long userId) {
        return transactionMapper.toOutputList(transactionRepository.findAllByCreatedByUserId(userId));
    }

    public List<TransactionOutput> getTransactionsByCustomerId(Long customerId) {
        return transactionMapper.toOutputList(transactionRepository.findAllByCustomerId(customerId));
    }
    
    public boolean existsByCustomerId(Long customerId) {
        return transactionRepository.existsByCustomerId(customerId);
    }

    public TransactionOutput getTransactionByReceiptNumber(Long receiptNumber) {
        return transactionRepository.findByReceiptNumber(receiptNumber)
            .map(transactionMapper::toOutput)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with receipt number: " + receiptNumber + "."));
    }

    public Map<String, Object> getSalesReportByDate(LocalDate date) {
        List<Transaction> transactions = transactionRepository.findAllByIssueDate(date);
        return buildSalesReport(transactions);
    }

    public Map<String, Object> getSalesReportBetweenDates(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = transactionRepository.findAllByIssueDateBetween(startDate, endDate);
        return buildSalesReport(transactions);
    }

    private Map<String, Object> buildSalesReport(List<Transaction> transactions) {
        int totalSales = transactions.size();
        BigDecimal totalSalesAmount = BigDecimal.ZERO;
        BigDecimal totalItemsSold = BigDecimal.ZERO;

        Map<String, BigDecimal> amountsByPaymentMethod = new HashMap<>();
        Map<String, BigDecimal> itemsSold = new HashMap<>();

        for (Transaction transaction : transactions) {
            if (transaction.isCancelled()) continue;
            
            totalSalesAmount = totalSalesAmount.add(
                transaction.getTransactionPayments().stream()
                    .map(TransactionPaymentMethod::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
            );
            
            totalItemsSold = totalItemsSold.add(
                transaction.getTransactionItems().stream()
                    .map(TransactionItem::getQuantity)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
            );

            for (TransactionPaymentMethod transactionPaymentMethod : transaction.getTransactionPayments()) {
                String paymentMethodName = transactionPaymentMethod.getPaymentMethod().getName();
                amountsByPaymentMethod.put(paymentMethodName, amountsByPaymentMethod.getOrDefault(paymentMethodName, BigDecimal.ZERO).add(transactionPaymentMethod.getAmount()));
            }

            for (TransactionItem transactionItem : transaction.getTransactionItems()) {
                String itemName = transactionItem.getItem().getName();
                itemsSold.put(itemName, itemsSold.getOrDefault(itemName, BigDecimal.ZERO).add(transactionItem.getQuantity()));
            }
        }

        List<Map.Entry<String, BigDecimal>> sortedItems = new ArrayList<>(itemsSold.entrySet());
        sortedItems.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        List<Map<String, Object>> topThreeItems = new ArrayList<>();
        for (int i = 0; i < Math.min(3, sortedItems.size()); i++) {
            Map<String, Object> itemInfo = new HashMap<>();
            itemInfo.put("name", sortedItems.get(i).getKey());
            itemInfo.put("quantity", sortedItems.get(i).getValue());
            topThreeItems.add(itemInfo);
        }

        Map<String, Object> report = new HashMap<>();
        report.put("totalSales", totalSales);
        report.put("totalSalesAmount", totalSalesAmount);
        report.put("totalItemsSold", totalItemsSold);
        report.put("amountsByPaymentMethod", amountsByPaymentMethod);
        report.put("topThreeItems", topThreeItems);

        return report;
    }
}
