package dev.mateorossello.merp.modules.sales.repositories.documents;

import dev.mateorossello.merp.modules.sales.models.documents.Note;
import dev.mateorossello.merp.modules.sales.models.documents.NoteType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Interface which extends JpaRepository to provide CRUD operations for Note entities.
 */

public interface NoteRepository extends JpaRepository<Note, Long> {
    Optional<Note> findByNumber(Long number);
    List<Note> findAllByIssueDate(LocalDate issueDate);
    List<Note> findAllByTransactionId(Long transactionId);
    List<Note> findAllByCreatedByUserId(Long createdByUserId);

    List<Note> findAllByInvoiceId(Long invoiceId);
    List<Note> findAllByNoteType(NoteType noteType);

    @Query("SELECT COALESCE(MAX(note.number), 0) FROM Note note")
    Long findMaxNumber();
}
