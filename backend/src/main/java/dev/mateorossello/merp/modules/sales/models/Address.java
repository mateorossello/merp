package dev.mateorossello.merp.modules.sales.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * Entity representing an address in the sales system.
 */

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Country is required")
    @Column(name = "country", nullable = false)
    private String country;

    @NotBlank(message = "Province is required")
    @Column(name = "province", nullable = false)
    private String province;

    @NotBlank(message = "City is required")
    @Column(name = "city", nullable = false)
    private String city;

    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "^[0-9]*$", message = "Postal code must contain only digits")
    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @NotBlank(message = "Street is required")
    @Column(name = "street", nullable = false)
    private String street;

    @NotBlank(message = "Street number is required")
    @Pattern(regexp = "^[0-9]*$", message = "Street number must contain only digits")
    @Column(name = "street_number", nullable = false)
    private String streetNumber;
}
