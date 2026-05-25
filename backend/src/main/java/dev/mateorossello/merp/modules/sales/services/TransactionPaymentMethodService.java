package dev.mateorossello.merp.modules.sales.services;

import dev.mateorossello.merp.modules.sales.repositories.TransactionPaymentMethodRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for managing TransactionPaymentMethod entities. Provides methods for retrieving transaction payment methods.
 */

@Service
@AllArgsConstructor
public class TransactionPaymentMethodService {
    private final TransactionPaymentMethodRepository transactionPaymentMethodRepository;

    //
    // Get methods
    //
    
    public boolean existsTransactionPaymentMethodByPaymentMethodId(Long paymentMethodId) {
        return transactionPaymentMethodRepository.existsByPaymentMethodId(paymentMethodId);
    }
}
