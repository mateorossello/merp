package dev.mateorossello.merp.modules.sales.services;

import dev.mateorossello.merp.modules.sales.repositories.TransactionItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for managing TransactionItem entities. Provides methods for retrieving transaction items.
 */

@Service
@AllArgsConstructor
public class TransactionItemService {
    private final TransactionItemRepository transactionItemRepository;

    //
    // Get methods
    //
    
    public boolean existsTransactionItemByItemId(Long itemId) {
        return transactionItemRepository.existsByItemId(itemId);
    }
}
