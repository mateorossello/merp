package dev.mateorossello.merp.modules.sales.services;

import dev.mateorossello.merp.exceptions.ResourceConflictException;
import dev.mateorossello.merp.exceptions.ResourceNotFoundException;
import dev.mateorossello.merp.modules.accounting.services.AccountService;
import dev.mateorossello.merp.modules.sales.dtos.PaymentMethodInput;
import dev.mateorossello.merp.modules.sales.dtos.PaymentMethodOutput;
import dev.mateorossello.merp.modules.sales.mappers.PaymentMethodMapper;
import dev.mateorossello.merp.modules.sales.models.PaymentMethod;
import dev.mateorossello.merp.modules.sales.repositories.PaymentMethodRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing PaymentMethod entities. Provides methods for creating, deleting, updating, and retrieving payment methods.
 */

@Service
@AllArgsConstructor
public class PaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentMethodMapper paymentMethodMapper;
    private final TransactionPaymentMethodService transactionPaymentMethodService;
    private final AccountService accountService;

    // 
    // Create methods
    //
    
    @Transactional
    public PaymentMethodOutput createPaymentMethod(PaymentMethodInput paymentMethodInput) {
        if (paymentMethodRepository.existsByName(paymentMethodInput.name())) {
            throw new ResourceConflictException("Payment method not created. A payment method with this name already exists.");
        }

        PaymentMethod paymentMethod = paymentMethodMapper.toEntity(paymentMethodInput);
        
        if (paymentMethodInput.accountId() != null) {
            paymentMethod.setAccount(accountService.getAccountById(paymentMethodInput.accountId()));
        }

        paymentMethod = paymentMethodRepository.save(paymentMethod);

        return paymentMethodMapper.toOutput(paymentMethod);
    }

    //
    // Delete method
    //

    @Transactional
    public void deletePaymentMethod(Long id) {
        PaymentMethod paymentMethod = getPaymentMethodById(id);

        if (transactionPaymentMethodService.existsTransactionPaymentMethodByPaymentMethodId(paymentMethod.getId())) {
            throw new ResourceConflictException("Cannot delete payment method because it has associated transactions.");
        }

        paymentMethodRepository.delete(paymentMethod);
    }

    //
    // Update method
    // 

    @Transactional
    public PaymentMethodOutput updatePaymentMethod(Long id, PaymentMethodInput paymentMethodInput) {
        PaymentMethod paymentMethod = getPaymentMethodById(id);

        paymentMethodRepository.findByName(paymentMethodInput.name()).ifPresent(existingPaymentMethod -> {
            if (!existingPaymentMethod.getId().equals(paymentMethod.getId())) {
                throw new ResourceConflictException("Payment method not updated. A payment method with this name already exists.");
            }
        });

        paymentMethod.setName(paymentMethodInput.name());

        if (paymentMethodInput.accountId() != null) {
            paymentMethod.setAccount(accountService.getAccountById(paymentMethodInput.accountId()));
        } else {
            paymentMethod.setAccount(null);
        }

        return paymentMethodMapper.toOutput(paymentMethodRepository.save(paymentMethod));
    }

    //
    // Get methods
    //

    public PaymentMethod getPaymentMethodById(Long id) {
        return paymentMethodRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Payment method not found."));
    }

    public PaymentMethodOutput getPaymentMethodDtoById(Long id) {
        return paymentMethodMapper.toOutput(getPaymentMethodById(id));
    }

    public List<PaymentMethod> getPaymentMethodsByIds(List<Long> ids) {
        return paymentMethodRepository.findAllById(ids);
    }

    public List<PaymentMethodOutput> getAllPaymentMethods() {
        return paymentMethodMapper.toOutputList(paymentMethodRepository.findAll());
    }

    public PaymentMethodOutput getPaymentMethodByName(String name) {
        return paymentMethodRepository.findByName(name)
            .map(paymentMethodMapper::toOutput)
            .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with name: " + name + "."));
    }
}
