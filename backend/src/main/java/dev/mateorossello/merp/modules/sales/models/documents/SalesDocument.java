package dev.mateorossello.merp.modules.sales.models.documents;

import dev.mateorossello.merp.modules.sales.models.Transaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Entity representing a sales document in the sales system.
 */

@Entity
@EntityListeners(AuditingEntityListener.class)
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

    @NotNull(message = "Number is required.")
    @Column(name = "number", unique = true, nullable = false)
    private Long number;

    @NotNull(message = "Issue date is required.")
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = true)
    private Transaction transaction;

    @CreatedBy
    @Column(name = "created_by_user_id", nullable = false, updatable = false)
    private Long createdByUserId;
}
