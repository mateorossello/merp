package dev.mateorossello.merp.modules.sales.controllers.documents;

import dev.mateorossello.merp.modules.sales.dtos.documents.NoteInput;
import dev.mateorossello.merp.modules.sales.dtos.documents.NoteOutput;
import dev.mateorossello.merp.modules.sales.models.documents.NoteType;
import dev.mateorossello.merp.modules.sales.services.documents.NoteService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for managing Note entities. Provides endpoints for creating and retrieving notes.
 */

@RestController
@RequestMapping("/sales/notes")
@AllArgsConstructor
public class NoteController {
    private final NoteService noteService;

    //
    // Create methods
    //

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGE_NOTES')")
    public ResponseEntity<NoteOutput> createNote(@Valid @RequestBody NoteInput noteInput) {
        return ResponseEntity.ok(noteService.createNote(noteInput));
    }

    //
    // Get methods
    //

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_NOTES')")
    public ResponseEntity<NoteOutput> getNoteById(@PathVariable Long id) {
        return ResponseEntity.ok(noteService.getNoteDtoById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_NOTES')")
    public ResponseEntity<List<NoteOutput>> getAllNotes() {
        return ResponseEntity.ok(noteService.getAllNotes());
    }

    @GetMapping("/number/{number}")
    @PreAuthorize("hasAuthority('VIEW_NOTES')")
    public ResponseEntity<NoteOutput> getNoteByNumber(@PathVariable Long number) {
        return ResponseEntity.ok(noteService.getNoteByNumber(number));
    }

    @GetMapping("/issue-date/{issueDate}")
    @PreAuthorize("hasAuthority('VIEW_NOTES')")
    public ResponseEntity<List<NoteOutput>> getNotesByIssueDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issueDate) {
        return ResponseEntity.ok(noteService.getNotesByIssueDate(issueDate));
    }

    @GetMapping("/transaction/{transactionId}")
    @PreAuthorize("hasAuthority('VIEW_NOTES')")
    public ResponseEntity<List<NoteOutput>> getNotesByTransactionId(@PathVariable Long transactionId) {
        return ResponseEntity.ok(noteService.getNotesByTransactionId(transactionId));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('VIEW_NOTES')")
    public ResponseEntity<List<NoteOutput>> getNotesByCreatedByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(noteService.getNotesByCreatedByUserId(userId));
    }

    @GetMapping("/invoice/{invoiceId}")
    @PreAuthorize("hasAuthority('VIEW_NOTES')")
    public ResponseEntity<List<NoteOutput>> getNotesByInvoiceId(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(noteService.getNotesByInvoiceId(invoiceId));
    }

    @GetMapping("/type/{noteType}")
    @PreAuthorize("hasAuthority('VIEW_NOTES')")
    public ResponseEntity<List<NoteOutput>> getNotesByType(@PathVariable NoteType noteType) {
        return ResponseEntity.ok(noteService.getNotesByNoteType(noteType));
    }
}
