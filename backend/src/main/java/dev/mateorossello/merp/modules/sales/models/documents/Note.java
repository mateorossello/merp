package dev.mateorossello.merp.modules.sales.models.documents;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Entity representing a debit or credit note in the sales system.
 */

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notes")
public class Note extends SalesDocument {
    @NotNull(message = "Invoice ID is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @NotNull(message = "Note type is required")
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NoteType noteType;

    @NotBlank(message = "Reason is required")
    @Builder.Default
    @Column(name = "reason", nullable = false)
    private String reason = "";

    @Builder.Default
    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoteItem> noteItems = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoteAdjustment> noteAdjustments = new ArrayList<>();

    public void addNoteItem(NoteItem noteItem) {
        noteItem.setNote(this);
        this.noteItems.add(noteItem);
    }

    public void removeNoteItem(NoteItem noteItem) {
        noteItem.setNote(null);
        this.noteItems.remove(noteItem);
    }

    public void addAdjustment(NoteAdjustment adjustment) {
        adjustment.setNote(this);
        this.noteAdjustments.add(adjustment);
    }

    public void removeAdjustment(NoteAdjustment adjustment) {
        adjustment.setNote(null);
        this.noteAdjustments.remove(adjustment);
    }
}
