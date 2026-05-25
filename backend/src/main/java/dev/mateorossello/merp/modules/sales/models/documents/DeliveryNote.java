package dev.mateorossello.merp.modules.sales.models.documents;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Entity representing a delivery note in the sales system.
 */

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "delivery_notes")
public class DeliveryNote extends SalesDocument {
    @NotNull
    @Column(name = "delivery_date", nullable = false)
    private LocalDate deliveryDate;

    @Builder.Default
    @Column(name = "cancelled", nullable = false)
    private boolean cancelled = false;

    @Column(name = "cancellation_reason", nullable = true)
    private String cancellationReason;

    @NotNull(message = "Delivery note type is required")
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DeliveryNoteType deliveryNoteType;

    @Builder.Default
    @OneToMany(mappedBy = "deliveryNote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryNoteItem> deliveryNoteItems = new ArrayList<>();

    public void addDeliveryItem(DeliveryNoteItem deliveryItem) {
        deliveryItem.setDeliveryNote(this);
        this.deliveryNoteItems.add(deliveryItem);
    }

    public void removeDeliveryItem(DeliveryNoteItem deliveryItem) {
        deliveryItem.setDeliveryNote(null);
        this.deliveryNoteItems.remove(deliveryItem);
    }
}
