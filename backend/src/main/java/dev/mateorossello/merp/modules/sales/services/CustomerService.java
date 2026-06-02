package dev.mateorossello.merp.modules.sales.services;

import dev.mateorossello.merp.exceptions.ResourceConflictException;
import dev.mateorossello.merp.exceptions.ResourceNotFoundException;
import dev.mateorossello.merp.modules.sales.dtos.CustomerInput;
import dev.mateorossello.merp.modules.sales.dtos.CustomerOutput;
import dev.mateorossello.merp.modules.sales.mappers.CustomerMapper;
import dev.mateorossello.merp.modules.sales.models.Address;
import dev.mateorossello.merp.modules.sales.models.Customer;
import dev.mateorossello.merp.modules.sales.models.FiscalConfiguration;
import dev.mateorossello.merp.modules.sales.models.FiscalType;
import dev.mateorossello.merp.modules.sales.repositories.CustomerRepository;
import dev.mateorossello.merp.modules.sales.repositories.TransactionRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing Customer entities. Provides methods for creating, deleting, updating and retrieving customers.
 */

@Service
@AllArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final FiscalConfigurationService fiscalConfigurationService;
    private final TransactionRepository transactionRepository;

    //
    // Create methods
    //

    @Transactional
    public CustomerOutput createCustomer(CustomerInput customerInput) {
        FiscalConfiguration fiscalConfiguration = fiscalConfigurationService.getFiscalConfiguration();
        Customer customer = customerMapper.toEntity(customerInput);

        if (customer.getLegalName().equals(fiscalConfiguration.getLegalName())) {
            throw new ResourceConflictException("Cannot create customer. Legal name matches the fiscal configuration legal name.");
        }

        if (customer.getCuit().equals(fiscalConfiguration.getCuit())) {
            throw new ResourceConflictException("Cannot create customer. CUIT matches the fiscal configuration CUIT.");
        }

        if (customer.getPhone().equals(fiscalConfiguration.getPhone())) {
            throw new ResourceConflictException("Cannot create customer. Phone matches the fiscal configuration phone.");
        }

        if (customer.getEmail().equals(fiscalConfiguration.getEmail())) {
            throw new ResourceConflictException("Cannot create customer. Email matches the fiscal configuration email.");
        }

        if (customerRepository.existsByLegalName(customer.getLegalName())) {
            throw new ResourceConflictException("Cannot create customer. A customer with this legal name already exists.");
        }

        if (customerRepository.existsByCuit(customer.getCuit())) {
            throw new ResourceConflictException("Cannot create customer. A customer with this CUIT already exists.");
        }

        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new ResourceConflictException("Cannot create customer. A customer with this email already exists.");
        }

        customer = customerRepository.save(customer);

        return customerMapper.toOutput(customer);
    }

    //
    // Delete methods
    //

    @Transactional
    public void deleteCustomer(Long id) {
        if (transactionRepository.existsByCustomerId(id)) {
            throw new ResourceConflictException("Cannot delete customer. It has associated transactions.");
        }

        Customer customer = getCustomerById(id);
        customerRepository.delete(customer);
    }

    // Update methods

    @Transactional
    public CustomerOutput updateCustomer(Long id, CustomerInput customerInput) {
        FiscalConfiguration fiscalConfiguration = fiscalConfigurationService.getFiscalConfiguration();
        Customer customer = getCustomerById(id);

        if (customerInput.legalName().equals(fiscalConfiguration.getLegalName())) {
            throw new ResourceConflictException("Cannot update customer. Legal name matches the fiscal configuration legal name.");
        }

        if (customerInput.cuit().equals(fiscalConfiguration.getCuit())) {
            throw new ResourceConflictException("Cannot update customer. CUIT matches the fiscal configuration CUIT.");
        }

        if (customerInput.phone().equals(fiscalConfiguration.getPhone())) {
            throw new ResourceConflictException("Cannot update customer. Phone matches the fiscal configuration phone.");
        }

        customerRepository.findByLegalName(customerInput.legalName()).ifPresent(existing -> {
            if (!existing.getId().equals(customer.getId())) {
                throw new ResourceConflictException("Cannot update customer. A customer with this legal name already exists.");
            }
        });

        customerRepository.findByCuit(customerInput.cuit()).ifPresent(existing -> {
            if (!existing.getId().equals(customer.getId())) {
                throw new ResourceConflictException("Cannot update customer. A customer with this CUIT already exists.");
            }
        });

        customerRepository.findByEmail(customerInput.email()).ifPresent(existing -> {
            if (!existing.getId().equals(customer.getId())) {
                throw new ResourceConflictException("Cannot update customer. A customer with this email already exists.");
            }
        });

        customer.setLegalName(customerInput.legalName());
        
        Address existingAddress = customer.getAddress();
        Address newAddress = customerMapper.toEntity(customerInput).getAddress();
        newAddress.setId(existingAddress.getId());
        
        if (!existingAddress.equals(newAddress)) {
            existingAddress.setCountry(newAddress.getCountry());
            existingAddress.setProvince(newAddress.getProvince());
            existingAddress.setCity(newAddress.getCity());
            existingAddress.setPostalCode(newAddress.getPostalCode());
            existingAddress.setStreet(newAddress.getStreet());
            existingAddress.setStreetNumber(newAddress.getStreetNumber());
        }
        
        customer.setAddress(existingAddress);
        customer.setCuit(customerInput.cuit());
        customer.setPhone(customerInput.phone());
        customer.setEmail(customerInput.email());

        customer.setFiscalType(customerInput.fiscalType());
        
        return customerMapper.toOutput(customerRepository.save(customer));
    }

    //
    // Get methods
    //

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found."));
    }

    public CustomerOutput getCustomerDtoById(Long id) {
        return customerMapper.toOutput(getCustomerById(id));
    }

    public List<CustomerOutput> getAllCustomers() {
        return customerMapper.toOutputList(customerRepository.findAll());
    }

    public CustomerOutput getCustomerByLegalName(String legalName) {
        return customerRepository.findByLegalName(legalName)
            .map(customerMapper::toOutput)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with legal name: " + legalName + "."));
    }

    public CustomerOutput getCustomerByCuit(String cuit) {
        return customerRepository.findByCuit(cuit)
            .map(customerMapper::toOutput)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with CUIT: " + cuit + "."));
    }

    public CustomerOutput getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
            .map(customerMapper::toOutput)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + email + "."));
    }

    public List<CustomerOutput> getAllCustomersByFiscalType(FiscalType fiscalType) {
        return customerRepository.findAllByFiscalType(fiscalType)
            .stream()
            .map(customerMapper::toOutput)
            .toList();
    }
}
