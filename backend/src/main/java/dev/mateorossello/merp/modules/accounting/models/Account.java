package dev.mateorossello.merp.modules.accounting.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entity representing an account in the accounting system.
 */

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @Builder.Default
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_account_id")
    private Account parentAccount = null;

    @NotBlank
    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @NotNull
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AccountType type = null;

    @NotBlank
    @Column(name = "name", unique = true, nullable = false)
    @EqualsAndHashCode.Include
    private String name;

    @Column(name = "description")
    private String description;

    @Builder.Default
    @Column(name = "receive_balance", nullable = false)
    private boolean receiveBalance = true;

    @Builder.Default
    @Column(name = "state", nullable = false)
    private boolean state = true;
}
