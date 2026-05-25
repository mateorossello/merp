package dev.mateorossello.merp.modules.sales.repositories.documents;

import dev.mateorossello.merp.modules.sales.models.documents.DeliveryNote;
import dev.mateorossello.merp.modules.sales.models.documents.DeliveryNoteType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Interface which extends JpaRepository to provide CRUD operations for DeliveryNote entities.
 */

public interface DeliveryNoteRepository extends JpaRepository<DeliveryNote, Long> {
    Optional<DeliveryNote> findByNumber(Long number);
    List<DeliveryNote> findAllByIssueDate(LocalDate issueDate);
    List<DeliveryNote> findAllByTransactionId(Long transactionId);
    List<DeliveryNote> findAllByCreatedByUserId(Long createdByUserId);

    List<DeliveryNote> findAllByDeliveryDate(LocalDate deliveryDate);
    List<DeliveryNote> findAllByCancelled(boolean cancelled);
    List<DeliveryNote> findAllByDeliveryNoteType(DeliveryNoteType type);

    @Query("SELECT COALESCE(MAX(deliveryNote.number), 0) FROM DeliveryNote deliveryNote")
    Long findMaxNumber();
}
