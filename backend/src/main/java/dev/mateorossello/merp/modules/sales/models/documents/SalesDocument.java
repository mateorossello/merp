package dev.mateorossello.merp.modules.sales.models.documents;

import dev.mateorossello.merp.modules.sales.models.Transaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Entity representing a sales document in the sales system.
 */

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sales_documents")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class SalesDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sales_document_id")
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull(message = "Number is required")
    @Column(name = "number", unique = true, nullable = false)
    private Long number;

    @NotNull(message = "Issue date is required")
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = true)
    private Transaction transaction;

    @NotNull(message = "Created by user ID is required")
    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;
}
