package dev.mateorossello.merp.modules.sales.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.*;

/**
 * Entity representing an item in the sales system.
 */

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Code cannot be blank")
    @Column(name = "code", unique = true, nullable = false)
    @EqualsAndHashCode.Include
    private String code;

    @NotBlank(message = "Name cannot be blank")
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;
    
    @Builder.Default
    @Column(name = "available", nullable = false)
    private boolean available = true;

    @NotNull
    @PositiveOrZero(message = "Unit price must be greater than or equal to 0")
    @Builder.Default
    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @NotNull
    @PositiveOrZero(message = "Current stock must be greater than or equal to 0")
    @Builder.Default
    @Column(name = "current_stock", precision = 10, scale = 2, nullable = false)
    private BigDecimal currentStock = BigDecimal.ZERO;

    @NotNull
    @PositiveOrZero(message = "Minimum stock must be greater than or equal to 0")
    @Builder.Default
    @Column(name = "minimum_stock", precision = 10, scale = 2, nullable = false)
    private BigDecimal minimumStock = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.0", message = "IVA must be greater than or equal to 0")
    @DecimalMax(value = "100.0", message = "IVA must be less than or equal to 100")
    @Builder.Default
    @Column(name = "iva", nullable = false)
    private BigDecimal iva = BigDecimal.ZERO;
}
