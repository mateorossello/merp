package dev.mateorossello.merp.AccountingModule.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.*;

/**
 * Entity representing a line in a journal entry in the accounting system.
 */

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "journal_entry_lines")
public class JournalEntryLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_id")
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_entry_id", nullable = false)
    private JournalEntry journalEntry;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Column(name = "amount", precision = 19, scale = 4, nullable = false)
    private BigDecimal amount;

    @NotNull
    @Column(name = "is_debit", nullable = false)
    private boolean debit;

    @Column(name = "reference")
    private String reference;
}
