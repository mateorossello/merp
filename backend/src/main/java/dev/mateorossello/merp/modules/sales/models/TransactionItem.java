package dev.mateorossello.merp.modules.sales.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.*;

/**
 * Entity representing an item line associated with a transaction.
 */

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction_items")
public class TransactionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_item_id")
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @NotNull
    @PositiveOrZero(message = "Unit price must be greater than or equal to 0")
    @Builder.Default
    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @NotNull
    @PositiveOrZero(message = "Unit cost must be greater than or equal to 0")
    @Builder.Default
    @Column(name = "unit_cost", nullable = false)
    private BigDecimal unitCost = BigDecimal.ZERO;

    @NotNull
    @Positive(message = "Quantity must be greater than 0")
    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @NotNull
    @DecimalMin(value = "0.0", message = "Discount must be greater than or equal to 0")
    @DecimalMax(value = "100.0", message = "Discount must be less than or equal to 100")
    @Builder.Default
    @Column(name = "discount", nullable = false)
    private BigDecimal discount = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.0", message = "IVA must be greater than or equal to 0")
    @DecimalMax(value = "100.0", message = "IVA must be less than or equal to 100")
    @Builder.Default
    @Column(name = "iva", nullable = false)
    private BigDecimal iva = BigDecimal.ZERO;

    @NotNull
    @Builder.Default
    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;
}
