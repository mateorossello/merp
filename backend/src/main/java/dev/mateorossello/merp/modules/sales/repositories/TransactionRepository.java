package dev.mateorossello.merp.modules.sales.repositories;

import dev.mateorossello.merp.modules.sales.models.Transaction;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Interface which extends JpaRepository to provide CRUD operations for Transaction entities.
 */

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByIssueDate(LocalDate issueDate);
    List<Transaction> findAllByIssueDateBetween(LocalDate startDate, LocalDate endDate);

    List<Transaction> findAllByCreatedByUserId(Long createdByUserId);
    boolean existsByCreatedByUserId(Long createdByUserId);

    List<Transaction> findAllByCustomerId(Long customerId);
    boolean existsByCustomerId(Long customerId);

    Optional<Transaction> findByReceiptNumber(Long receiptNumber);
    boolean existsByReceiptNumber(Long receiptNumber);

    List<Transaction> findAllByCancelled(boolean cancelled);

    @Query("SELECT COALESCE(MAX(transaction.receiptNumber), 0) FROM Transaction transaction")
    Long findMaxReceiptNumber();
}
