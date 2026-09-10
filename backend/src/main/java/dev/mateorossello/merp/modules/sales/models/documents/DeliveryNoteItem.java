package dev.mateorossello.merp.modules.sales.models.documents;

import dev.mateorossello.merp.modules.sales.models.Item;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.*;

/**
 * Entity representing an item in a delivery note in the sales system.
 */

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "delivery_note_items")
public class DeliveryNoteItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_note_item_id")
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull(message = "Item is required.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @NotNull(message = "Quantity is required.")
    @Positive(message = "Quantity must be greater than 0.")
    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @NotNull(message = "Delivery note is required.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_note_id", nullable = false)
    private DeliveryNote deliveryNote;
}
