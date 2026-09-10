package dev.mateorossello.merp.modules.sales.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.Valid;
import lombok.*;

/**
 * Entity representing a customer in the sales system.
 */

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Legal name is required.")
    @Column(name = "legal_name", unique = true, nullable = false)
    @EqualsAndHashCode.Include
    private String legalName;

    @NotNull
    @Valid
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @NotBlank(message = "CUIT is required.")
    @Pattern(regexp = "^[0-9]{11}$", message = "CUIT must contain only 11 digits.")
    @Column(name = "cuit", unique = true, nullable = false)
    private String cuit;

    @NotBlank(message = "Phone is required.")
    @Pattern(regexp = "^[0-9]*$", message = "Phone must contain only digits.")
    @Column(name = "phone", nullable = false)
    private String phone;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email should be valid.")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @NotNull
    @Column(name = "fiscal_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private FiscalType fiscalType;
}
