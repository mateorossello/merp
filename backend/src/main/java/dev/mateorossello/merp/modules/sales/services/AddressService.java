package dev.mateorossello.merp.modules.sales.services;

import dev.mateorossello.merp.exceptions.ResourceNotFoundException;
import dev.mateorossello.merp.modules.sales.dtos.AddressOutput;
import dev.mateorossello.merp.modules.sales.mappers.AddressMapper;
import dev.mateorossello.merp.modules.sales.models.Address;
import dev.mateorossello.merp.modules.sales.repositories.AddressRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for managing Address entities. Provides methods for retrieving addresses.
 */

@Service
@AllArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    //
    // Get methods
    //

    public Address getAddressById(Long id) {
        return addressRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Address not found."));
    }

    public AddressOutput getAddressDtoById(Long id) {
        return addressMapper.toOutput(getAddressById(id));
    }

    public List<AddressOutput> getAllAddresses() {
        return addressMapper.toOutputList(addressRepository.findAll());
    }
}
