package dev.mateorossello.merp.modules.sales.models.documents;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.*;

/**
 * Entity representing an item in a note in the sales system.
 */

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "note_items")
public class NoteItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "note_item_id")
    @EqualsAndHashCode.Include
    private Long id;
    
    @NotNull(message = "Item ID is required")
    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @NotNull(message = "Note ID is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;
}
