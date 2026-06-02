package dev.mateorossello.merp.modules.sales.repositories.documents;

import dev.mateorossello.merp.modules.sales.models.documents.Invoice;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Interface which extends JpaRepository to provide CRUD operations for Invoice entities.
 */

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByNumber(Long number);
    List<Invoice> findAllByIssueDate(LocalDate issueDate);
    List<Invoice> findAllByTransactionId(Long transactionId);
    List<Invoice> findAllByCreatedByUserId(Long createdByUserId);
    
    List<Invoice> findAllByDueDate(LocalDate dueDate);

    @Query("SELECT COALESCE(MAX(invoice.number), 0) FROM Invoice invoice")
    Long findMaxNumber();
}
