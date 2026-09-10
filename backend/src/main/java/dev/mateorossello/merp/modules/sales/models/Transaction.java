package dev.mateorossello.merp.modules.sales.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

/**
 * Entity representing a sales transaction in the sales system.
 */

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @NotNull
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @NotNull
    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull
    @Column(name = "receipt_number", unique = true, nullable = false)
    private Long receiptNumber;

    @Builder.Default
    @Column(name = "cancelled", nullable = false)
    private boolean cancelled = false;

    @NotNull
    @PositiveOrZero(message = "Total must be greater than or equal to 0.")
    @Builder.Default
    @Column(name = "total", precision = 10, scale = 2, nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

    @Builder.Default
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionItem> transactionItems = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionPaymentMethod> transactionPayments = new ArrayList<>();

    public void addTransactionItem(TransactionItem transactionItem) {
        transactionItem.setTransaction(this);
        this.transactionItems.add(transactionItem);
    }

    public void addTransactionPayment(TransactionPaymentMethod transactionPayment) {
        transactionPayment.setTransaction(this);
        this.transactionPayments.add(transactionPayment);
    }
}
