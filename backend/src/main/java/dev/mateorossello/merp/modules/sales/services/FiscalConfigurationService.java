package dev.mateorossello.merp.modules.sales.services;

import dev.mateorossello.merp.exceptions.ResourceConflictException;
import dev.mateorossello.merp.exceptions.ResourceNotFoundException;
import dev.mateorossello.merp.modules.sales.dtos.FiscalConfigurationInput;
import dev.mateorossello.merp.modules.sales.dtos.FiscalConfigurationOutput;
import dev.mateorossello.merp.modules.sales.mappers.FiscalConfigurationMapper;
import dev.mateorossello.merp.modules.sales.models.Address;
import dev.mateorossello.merp.modules.sales.models.FiscalConfiguration;
import dev.mateorossello.merp.modules.sales.repositories.FiscalConfigurationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing FiscalConfiguration entities. Provides methods for creating, updating and retrieving the fiscal configuration.
 */

@Service
@AllArgsConstructor
public class FiscalConfigurationService {
    private final FiscalConfigurationRepository fiscalConfigurationRepository;
    private final FiscalConfigurationMapper fiscalConfigurationMapper;

    //
    // Create methods
    //
    
    @Transactional
    public FiscalConfigurationOutput createFiscalConfiguration(FiscalConfigurationInput fiscalConfigurationInput) {
        if (fiscalConfigurationRepository.count() != 0) {
            throw new ResourceConflictException("A fiscal configuration already exists. Contact the administrator.");
        }

        FiscalConfiguration fiscalConfiguration = fiscalConfigurationMapper.toEntity(fiscalConfigurationInput);
        fiscalConfiguration = fiscalConfigurationRepository.save(fiscalConfiguration);

        return fiscalConfigurationMapper.toOutput(fiscalConfiguration);
    }

    //
    // Update methods
    //

    @Transactional
    public FiscalConfigurationOutput updateFiscalConfiguration(FiscalConfigurationInput fiscalConfigurationInput) {
        if (fiscalConfigurationRepository.count() == 0) {
            throw new ResourceNotFoundException("No active fiscal configuration exists.");
        }

        FiscalConfiguration fiscalConfiguration = fiscalConfigurationRepository.findAll().get(0);
        FiscalConfiguration mappedUpdate = fiscalConfigurationMapper.toEntity(fiscalConfigurationInput);

        fiscalConfiguration.setLegalName(mappedUpdate.getLegalName());
        
        Address existingAddress = fiscalConfiguration.getAddress();
        Address newAddress = mappedUpdate.getAddress();
        newAddress.setId(existingAddress.getId());

        if (!existingAddress.equals(newAddress)) {
            existingAddress.setCountry(newAddress.getCountry());
            existingAddress.setProvince(newAddress.getProvince());
            existingAddress.setCity(newAddress.getCity());
            existingAddress.setPostalCode(newAddress.getPostalCode());
            existingAddress.setStreet(newAddress.getStreet());
            existingAddress.setStreetNumber(newAddress.getStreetNumber());
        }
        
        fiscalConfiguration.setAddress(existingAddress);
        fiscalConfiguration.setCuit(mappedUpdate.getCuit());
        fiscalConfiguration.setPhone(mappedUpdate.getPhone());
        fiscalConfiguration.setEmail(mappedUpdate.getEmail());
        
        fiscalConfiguration.setFiscalType(mappedUpdate.getFiscalType());
        
        fiscalConfiguration = fiscalConfigurationRepository.save(fiscalConfiguration);

        return fiscalConfigurationMapper.toOutput(fiscalConfiguration);
    }

    //
    // Get methods
    //

    public FiscalConfiguration getFiscalConfiguration() {
        if (fiscalConfigurationRepository.count() == 0) {
            throw new ResourceNotFoundException("No active fiscal configuration exists.");
        }

        return fiscalConfigurationRepository.findAll().get(0);
    }

    public FiscalConfigurationOutput getFiscalConfigurationDto() {
        return fiscalConfigurationMapper.toOutput(getFiscalConfiguration());
    }
}
